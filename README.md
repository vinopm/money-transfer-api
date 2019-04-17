# money-transfer-api

Dependencies: Only JUnit for unit tests, and Maven for building.

Maven pom.xml file configurable to change port number of RESTful service. Default set to: 65457.

GET & DELETE methods pass parameters through URI

PUT methods pass parameters in request body with format: application/x-www-form-urlencoded

RESTful Endpoints:
- Transfer (/transfer)
    - Create (/transfer/create) accepts PUT
        [http://hostname:port/account/create]
        - Description: Endpoint to carry out a transfer of money between two accounts.
        - Required parameters: 
            - transaction_id (UUID)
            - from (UUID)
            - to (UUID)
            - amount (amount without currency symbol)
        
    - Deposit (/transfer/deposit) accepts PUT
        [http://hostname:port/transfer/deposit]
        - Description: Endpoint to deposit money into an account.
        - Required parameters:
            - transaction_id (UUID)
            - account_id (UUID)
            - amount (amount without currency symbol)
            
    - Withdraw (/transfer/withdraw) accepts PUT
        [http://hostname:port/transfer/withdraw]
        - Description: Endpoint to withdraw money from an account.
        - Required parameters:
            - transaction_id (UUID)
            - account_id (UUID)
            - amount (amount without currency symbol)
            
- Account (/account)
    - Create (/account/create) accepts PUT
        [http://hostname:port/account/create]
        - Description: Endpoint to create a new account.
        - Required parameters:
            - account_id (UUID)
            
    - Delete (/account/delete) accepts DELETE (via URI)
        [http://hostname:port/account/delete]
        - Description: Endpoint to delete an account.
        - Required parameters:
            - account_id (UUID)
    
    - Balance (/account/balance) accepts GET
        [http://hostname:port/account/balance]
        - Description: Endpoint to get current balance for specified account.
        - Required parameters:
            - account_id (UUID)
            
- History (/history) accepts GET
    [http://hostname:port/history]
    - Description: Endpoint which provides a list of transfers (between accounts, deposits, withdrawals) for a given  account.
    - Required parameters:
        - account_id (UUID)
        
- UUID (/uuid)
    - Create (/uuid/create) accepts GET
        [http://hostname:port/uuid/create]
        - Description: Endpoint which generates a UUID which can be then used to create transfer requests. The idea is to call this endpoint before requesting a transfer API. In cases where a connection is dropped after making the request, the client can send the request again with the unique transaction UUID. The RESTful service would know if this transaction has already been processed and would respond to the client appropriately.
        
 List of response status codes:
    
    OK = 200, 
    
    BAD_REQUEST = 400, 
    
    NOT_FOUND = 404, 
    
    METHOD_NOT_ALLOWED = 405, 
    
    NOT_ACCEPTABLE_FORMAT = 406, 
    
    INTERNAL_SERVER_ERROR = 500


To run the application:

    1. mvn install
    
    2. mvn exec:exec
    
Configuration for this maven goal (mvn exec:exec is in pom.xml). JVM heap size set to 2GB by default - this is configurable. The port number is 65457 by default. This is also configurable.
                    
(Please note when running: may need to increase default Xmx heap size as data is stored in memory for this implementation).
