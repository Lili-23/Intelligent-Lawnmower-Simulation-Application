
1. In backend, for simplicity, we only consider one request and one response per time.
  No multiple requests at the same time.
  
2. For auto run mode, the front end will use loop to call backend next api.

3. The contract between frontend and backend is
https://github.gatech.edu/zwang959/A7-10/blob/master/Project/Code/MowersSystem/src/main/java/com/a710/cs6310/model/form/SystemStatus.java

4. For easy backend testing, we can leverage the simulateAll api. 
    Steps:
    http://localhost:8080/api/read/scenario2.csv
    Then http://localhost:8080/api/simulate/all  or http://localhost:8080/api/simulate/next
