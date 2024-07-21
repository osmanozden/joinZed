curl -X POST "http://localhost:8080/accounts/exchange" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "userId=1" \
-d "fromCurrency=USD" \
-d "toCurrency=TRY" \
-d "amount=100.00"

--------------------------
curl -X GET "http://localhost:8080/accounts/balance?userId=1&currency=USD"

--------------------------
curl -X POST "http://localhost:8080/accounts/withdraw" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "userId=1" \
-d "currency=USD" \
-d "amount=50.00"

---------------------------

curl -X POST "http://localhost:8080/accounts/deposit" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "userId=1" \
-d "currency=USD" \
-d "amount=100.00"
---------------------------

curl -X POST "http://localhost:8080/users/create" \
-H "Content-Type: application/json" \
-d '{"name":"Alice", "usdBalance":1500.00, "tryBalance":3000.00}'

-------------------------------
curl -X GET "http://localhost:8080/accounts/getAllAccounts"

-------------------------------

-------------------------------

-------------------------------

