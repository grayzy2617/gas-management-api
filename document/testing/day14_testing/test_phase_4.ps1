# Script kiểm thử luồng Khách hàng, Giỏ hàng và Đơn hàng (Phase 4)
$BASE_URL = "http://localhost:8080/api/v1"
$TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnYXNwcm8udm4iLCJzdWIiOiIwOTg3NjU0MzIxIiwiZXhwIjoxNzg2OTU1MDk4LCJpYXQiOjE3ODY4Njg2OTgsImp0aSI6IjYzNzU4OTFmLThmZjEtNGRmNC1hNmJkLTI5NDViMTM1Y2E4MyIsInNjb3BlIjoiQ1VTVE9NRVIifQ.OqZyf3MGuNHOkjCZWYFHrtw_fTpNN0j8buNb-TgaQZw"
$HEADERS = @{
    "Authorization" = "Bearer $TOKEN"
    "Content-Type"  = "application/json; charset=utf-8"
}

Write-Host "========================================="
Write-Host "1. GET CUSTOMER PROFILE"
Write-Host "========================================="
$profileResponse = Invoke-RestMethod -Uri "$BASE_URL/customers/me/profile" -Method Get -Headers $HEADERS
$profileResponse | ConvertTo-Json -Depth 5

Write-Host "========================================="
Write-Host "2. UPDATE VAT INFO"
Write-Host "========================================="
$vatBody = @{
    taxCode = "0312345678"
    companyName = "Công ty TNHH ABC"
    invoiceAddress = "123 Lê Lợi, Quận 1, TP.HCM"
} | ConvertTo-Json
$vatResponse = Invoke-RestMethod -Uri "$BASE_URL/customers/me/vat-info" -Method Put -Headers $HEADERS -Body $vatBody
$vatResponse | ConvertTo-Json -Depth 5

Write-Host "========================================="
Write-Host "3. ADD ITEM TO CART"
Write-Host "========================================="
$cartBody = @{
    productId = 1
    quantity = 2
    hasExchangeShell = $true
} | ConvertTo-Json
$addCartResponse = Invoke-RestMethod -Uri "$BASE_URL/cart/items" -Method Post -Headers $HEADERS -Body $cartBody
$addCartResponse | ConvertTo-Json -Depth 5

Write-Host "========================================="
Write-Host "4. GET MY CART"
Write-Host "========================================="
$cartResponse = Invoke-RestMethod -Uri "$BASE_URL/cart/items" -Method Get -Headers $HEADERS
$cartResponse | ConvertTo-Json -Depth 5

Write-Host "========================================="
Write-Host "5. CREATE ORDER"
Write-Host "========================================="
$orderBody = @{
    deliveryAddress = "123 Nguyen Trai, Quan 1, TP.HCM"
    distanceKm = 3.5
    paymentMethod = "COD"
    notes = "Giao gap"
    items = @(
        @{
            productId = 1
            quantity = 2
            hasExchangeShell = $true
        }
    )
} | ConvertTo-Json

try {
    $orderResponse = Invoke-RestMethod -Uri "$BASE_URL/orders" -Method Post -Headers $HEADERS -Body $orderBody
    $orderResponse | ConvertTo-Json -Depth 5
} catch {
    Write-Host "Error creating order: $_"
    $_.Exception.Response.GetResponseStream() | %{ (New-Object IO.StreamReader($_)).ReadToEnd() }
}
