# money-transfer-api

Dependencies: Only JUnit for unit tests, and Maven for building.

Maven pom.xml file configurable to change port number of RESTful service. Default set to: 65457.

RESTful Endpoints:
- Transfer (/transfer)
    - Create (/transfer/create) accepts PUT
        - Description: Endpoint to carry out a transfer of money between two accounts.
        - Required parameters: 
            - transaction_id (UUID)
            - from (UUID)
            - to (UUID)
            - amount (amount without currency symbol)
        
    - Deposit (/transfer/deposit) accepts PUT
        - Description: Endpoint to deposit money into an account.
        - Required parameters:
            - transaction_id (UUID)
            - account_id (UUID)
            - amount (amount without currency symbol)
            
    - Withdraw (/transfer/withdraw) accepts PUT
        - Description: Endpoint to withdraw money from an account.
        - Required parameters:
            - transaction_id (UUID)
            - account_id (UUID)
            - amount (amount without currency symbol)
            
- Account (/account)
    - Create (/account/create) accepts PUT
        - Description: Endpoint to create a new account.
        - Required parameters:
            - account_id (UUID)
            
    - Delete (/account/delete) accepts DELETE (via URI)
        - Description: Endpoint to delete an account.
        - Required parameters:
            - account_id (UUID)
    
    - Balance (/account/balance) accepts GET
        - Description: Endpoint to get current balance for specified account.
        - Required parameters:
            - account_id (UUID)
            
- History (/history) accepts GET
    - Description: Endpoint which provides a list of transfers (between accounts, deposits, withdrawals) for a given  account.
    - Required parameters:
        - account_id (UUID)
        
- UUID (/uuid)
    - Create (/uuid/create) accepts GET
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
    
    2. mvn exec:Java

(Please note when running: may need to increase default Xmx heap size as data is stored in memory for this implementation).
