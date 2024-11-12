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
