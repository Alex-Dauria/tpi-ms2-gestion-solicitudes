<#
.SYNOPSIS
    Registra la línea base del comportamiento externo del sistema.

.DESCRIPTION
    Compila la versión del código que haya en la carpeta, la levanta con los datos de prueba
    iniciales, ejecuta siempre los mismos 12 pedidos HTTP y guarda el código y la respuesta de
    cada uno en un archivo Markdown.

    La fecha del día se reemplaza por <HOY> para que se puedan comparar corridas de días distintos.
    Con -CompararCon, compara caso por caso contra una corrida anterior y agrega el resultado al final.

.EXAMPLE
    .\scripts\linea_base.ps1 -Salida docs\evidencias\pruebas\linea_base_estado_inicial.md

.EXAMPLE
    .\scripts\linea_base.ps1 -Salida docs\evidencias\pruebas\linea_base_estado_refactorizado.md -CompararCon docs\evidencias\pruebas\linea_base_estado_inicial.md
#>
param(
    [Parameter(Mandatory = $true)][string]$Salida,
    [string]$CompararCon,
    [int]$Puerto = 8080
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Net.Http

$raiz = Split-Path -Parent $PSScriptRoot
$base = "http://localhost:$Puerto"
$hoy = (Get-Date).ToString('yyyy-MM-dd')
$utf8SinBom = New-Object System.Text.UTF8Encoding $false

$casos = @(
    @{ Nombre = 'Registrar solicitud de un estudiante existente (prioridad calculada por tipo)'; Metodo = 'POST'; Ruta = '/solicitudes'
       Cuerpo = '{"legajo":"11002","tipo":"equivalencia","descripcion":"Equivalencia de Inglés técnico aprobada en otra institución"}' }
    @{ Nombre = 'Registrar solicitud de un estudiante nuevo (alta automática)'; Metodo = 'POST'; Ruta = '/solicitudes'
       Cuerpo = '{"legajo":"12001","nombre":"Sofía Castro","email":"Sofia.Castro@alumnos.example.edu.ar","tipo":"CERTIFICADO","descripcion":"Certificado de materias aprobadas para beca"}' }
    @{ Nombre = 'Rechazo por validación (descripción corta)'; Metodo = 'POST'; Ruta = '/solicitudes'
       Cuerpo = '{"legajo":"10567","tipo":"OTRO","descripcion":"corta"}' }
    @{ Nombre = 'Rechazo por regla de negocio (ya tiene una solicitud abierta del mismo tipo)'; Metodo = 'POST'; Ruta = '/solicitudes'
       Cuerpo = '{"legajo":"10234","tipo":"CERTIFICADO","descripcion":"Otro certificado de alumno regular"}' }
    @{ Nombre = 'Asignar responsable (pasa automáticamente a EN_REVISION)'; Metodo = 'PUT'; Ruta = '/solicitudes/1/responsable'
       Cuerpo = '{"responsableId":1}' }
    @{ Nombre = 'Asignar responsable inactivo'; Metodo = 'PUT'; Ruta = '/solicitudes/5/responsable'
       Cuerpo = '{"responsableId":4}' }
    @{ Nombre = 'Cambiar estado EN_REVISION -> RESUELTA'; Metodo = 'PUT'; Ruta = '/solicitudes/1/estado'
       Cuerpo = '{"estado":"RESUELTA","comentario":"Certificado enviado por correo"}' }
    @{ Nombre = 'Transición no permitida (RECHAZADA es estado final)'; Metodo = 'PUT'; Ruta = '/solicitudes/4/estado'
       Cuerpo = '{"estado":"PENDIENTE"}' }
    @{ Nombre = 'Buscar / filtrar: solicitudes de prioridad ALTA'; Metodo = 'GET'; Ruta = '/solicitudes?prioridad=alta' }
    @{ Nombre = 'Buscar / filtrar: solicitudes vencidas'; Metodo = 'GET'; Ruta = '/solicitudes?vencidas=true' }
    @{ Nombre = 'Consultar una solicitud inexistente'; Metodo = 'GET'; Ruta = '/solicitudes/99' }
    @{ Nombre = 'Reporte resumen'; Metodo = 'GET'; Ruta = '/reportes/resumen' }
)

function Resolver-Ruta([string]$ruta) {
    if ([System.IO.Path]::IsPathRooted($ruta)) { return $ruta }
    return [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $ruta))
}

function Puerto-Ocupado {
    try {
        $tcp = New-Object System.Net.Sockets.TcpClient
        $tcp.Connect('localhost', $Puerto)
        $tcp.Close()
        return $true
    } catch {
        return $false
    }
}

function Enviar($cliente, [string]$metodo, [string]$ruta, [string]$cuerpo) {
    $pedido = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new($metodo), "$base$ruta")
    if ($cuerpo) {
        $pedido.Content = [System.Net.Http.StringContent]::new($cuerpo, [System.Text.Encoding]::UTF8, 'application/json')
    }
    $respuesta = $cliente.SendAsync($pedido).Result
    $bytes = $respuesta.Content.ReadAsByteArrayAsync().Result
    return [pscustomobject]@{
        Codigo = [int]$respuesta.StatusCode
        Cuerpo = [System.Text.Encoding]::UTF8.GetString($bytes)
    }
}

# Separa un archivo de línea base en sus casos (lo que sigue a cada "### "), sin el encabezado ni la comparación
function Leer-Casos([string]$archivo) {
    $texto = [System.IO.File]::ReadAllText($archivo, [System.Text.Encoding]::UTF8) -replace "`r`n", "`n"
    $texto = ($texto -split '(?m)^## Comparación')[0]
    $partes = $texto -split '(?m)^### '
    return $partes[1..($partes.Count - 1)] | ForEach-Object { $_.Trim() }
}

$salidaCompleta = Resolver-Ruta $Salida
$compararCompleta = if ($CompararCon) { Resolver-Ruta $CompararCon } else { $null }
if ($compararCompleta -and -not (Test-Path $compararCompleta)) { throw "No existe el archivo a comparar: $compararCompleta" }

Push-Location $raiz
$app = $null
$cliente = $null
try {
    if (Puerto-Ocupado) { throw "El puerto $Puerto está ocupado. Frenar la aplicación que esté corriendo (Ctrl+C) y volver a ejecutar." }

    if (Test-Path datos) { Remove-Item -Recurse -Force datos }

    Write-Host 'Compilando...'
    & .\mvnw.cmd -q -DskipTests package
    if ($LASTEXITCODE -ne 0) { throw 'Falló la compilación.' }
    $jar = Get-ChildItem target\*.jar | Where-Object { $_.Name -notlike '*.original' } | Select-Object -First 1

    Write-Host 'Levantando la aplicación...'
    $log = Join-Path $env:TEMP 'linea_base_app.log'
    $app = Start-Process java -ArgumentList '-jar', "`"$($jar.FullName)`"", "--server.port=$Puerto" `
        -PassThru -NoNewWindow -RedirectStandardOutput $log -RedirectStandardError "$log.err"

    $cliente = New-Object System.Net.Http.HttpClient
    $lista = $false
    for ($i = 0; $i -lt 90 -and -not $lista; $i++) {
        if ($app.HasExited) { throw "La aplicación se cerró al arrancar. Ver $log" }
        try {
            $lista = $cliente.GetAsync("$base/responsables").Result.IsSuccessStatusCode
        } catch {
            Start-Sleep -Seconds 1
        }
    }
    if (-not $lista) { throw "La aplicación no respondió en 90 segundos. Ver $log" }

    $version = (& git describe --tags --always --dirty 2>$null)
    $commit = (& git rev-parse --short HEAD 2>$null)

    $lineas = New-Object System.Collections.Generic.List[string]
    $lineas.Add('# Línea base del comportamiento externo')
    $lineas.Add('')
    $lineas.Add("- **Versión del código:** ``$version`` (commit ``$commit``)")
    $lineas.Add("- **Fecha de ejecución:** $((Get-Date).ToString('yyyy-MM-dd HH:mm'))")
    $lineas.Add('- **Generado con:** `scripts/linea_base.ps1`')
    $lineas.Add('- **Datos de partida:** datos de prueba sintéticos (se borra `datos/` antes de arrancar)')
    $lineas.Add('- La fecha del día de la ejecución se reemplaza por `<HOY>` para poder comparar corridas de días distintos.')
    $lineas.Add('')
    $lineas.Add('| Caso | Pedido | Código |')
    $lineas.Add('|---|---|---|')

    $detalle = New-Object System.Collections.Generic.List[string]
    $n = 0
    foreach ($caso in $casos) {
        $n++
        $r = Enviar $cliente $caso.Metodo $caso.Ruta $caso.Cuerpo
        $cuerpoRespuesta = ($r.Cuerpo -replace "`r`n", "`n").Replace($hoy, '<HOY>').Trim()
        Write-Host ("  {0,2}. {1} {2} -> {3}" -f $n, $caso.Metodo, $caso.Ruta, $r.Codigo)

        $lineas.Add("| $n | ``$($caso.Metodo) $($caso.Ruta)`` | $($r.Codigo) |")

        $detalle.Add("### $n. $($caso.Nombre)")
        $detalle.Add('')
        $detalle.Add("``$($caso.Metodo) $($caso.Ruta)``")
        $detalle.Add('')
        if ($caso.Cuerpo) {
            $detalle.Add('```json')
            $detalle.Add($caso.Cuerpo)
            $detalle.Add('```')
            $detalle.Add('')
        }
        $detalle.Add("**Respuesta:** ``$($r.Codigo)``")
        $detalle.Add('')
        $detalle.Add('```json')
        $detalle.Add($cuerpoRespuesta)
        $detalle.Add('```')
        $detalle.Add('')
    }
    $lineas.Add('')
    $lineas.AddRange($detalle)

    $carpeta = Split-Path -Parent $salidaCompleta
    if (-not (Test-Path $carpeta)) { New-Item -ItemType Directory -Force $carpeta | Out-Null }
    [System.IO.File]::WriteAllText($salidaCompleta, ($lineas -join "`n"), $utf8SinBom)

    if ($compararCompleta) {
        $anteriores = @(Leer-Casos $compararCompleta)
        $actuales = @(Leer-Casos $salidaCompleta)
        $iguales = 0
        $tabla = New-Object System.Collections.Generic.List[string]
        $tabla.Add('')
        $tabla.Add("## Comparación con ``$(Split-Path -Leaf $compararCompleta)``")
        $tabla.Add('')
        $tabla.Add('| Caso | Resultado |')
        $tabla.Add('|---|---|')
        for ($i = 0; $i -lt $actuales.Count; $i++) {
            $igual = ($i -lt $anteriores.Count) -and ($actuales[$i] -ceq $anteriores[$i])
            if ($igual) { $iguales++ }
            $tabla.Add("| $($i + 1) | $(if ($igual) { 'Igual' } else { '**Distinto**' }) |")
        }
        $resumen = "$iguales de $($actuales.Count) casos iguales"
        $tabla.Add('')
        $tabla.Add("**Resultado:** $resumen.")
        [System.IO.File]::AppendAllText($salidaCompleta, "`n" + ($tabla -join "`n") + "`n", $utf8SinBom)
        Write-Host ''
        Write-Host "Comparación: $resumen" -ForegroundColor $(if ($iguales -eq $actuales.Count) { 'Green' } else { 'Red' })
    }

    Write-Host ''
    Write-Host "Línea base guardada en $salidaCompleta"
}
finally {
    if ($cliente) { $cliente.Dispose() }
    if ($app -and -not $app.HasExited) { Stop-Process -Id $app.Id -Force }
    Pop-Location
}
