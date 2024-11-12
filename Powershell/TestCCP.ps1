#Adding below code to skip tls cert validation
Add-Type @"
using System.Net;
using System.Security.Cryptography.X509Certificates;
public class TrustAllCertsPolicy : ICertificatePolicy {
    public bool CheckValidationResult(
    ServicePoint srvPoint, X509Certificate certificate,
    WebRequest request, int certificateProblem) {
        return true;
    }
}
"@

[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy

# Set Tls versions
$allProtocols = [System.Net.SecurityProtocolType]'Ssl3,Tls,Tls11,Tls12'
[System.Net.ServicePointManager]::SecurityProtocol = $allProtocols
#End of skiping TLS Valitation part

function Get-AIMPassword ([string]$PVWA_URL, [string]$AppID, [string]$Safe, [string]$ObjectName) {
 
    # Declaration
    $fetchAIMPassword = "${PVWA_URL}/AIMWebService/api/Accounts?AppID=${AppID}&Safe=${Safe}&Folder=Root&Object=${ObjectName}"

    # Execution
    try {
        $response = Invoke-RestMethod -Uri $fetchAIMPassword -Method GET `
			-ContentType "application/json" -ErrorVariable aimResultErr `
			-Certificate (Get-PfxCertificate certs\clients.cybr.huydo.net.pfx )
        Return $response.content
    }
    catch {
        Write-Host "StatusCode: " $_.Exception.Response.StatusCode.value__
        Write-Host "StatusDescription: " $_.Exception.Response.StatusDescription
        Write-Host "Response: " $_.Exception.Message
        Return $false
    }
}

$password = Get-AIMPassword -PVWA_URL "https://172.16.100.21" -AppID "TestingCP_UAT" -Safe "TestingCP_Safe" -ObjectName "testcp01" -UseDefaultCredentials

Write-Host "Your password is: ${password}" 
pause "Press any key to continue"
