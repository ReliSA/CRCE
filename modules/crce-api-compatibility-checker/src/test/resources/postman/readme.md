# Test configuration

This directory contains Postman configuration files used to automatically test API comparison via REST API.

## Postman configuration

There are two configuration files for Postman, both of them can be (and should be) imported to Postman. In order to use them, follow these steps:
 
 1. Import `CRCE-dev.postman_environment.json` to Postman
    - this file contains environment variables such as hostname with running CRCE instance and path to CRCE REST API
 2. Import `CRCE-API tests.postman_collection.json` to Postman
    - this file contains definition of collection that is to be used to call comparison API as well as the definition of test script
    used to check correct results   

## Test data


Use files in `csv` directory as source files for Postman Collection Runner. The file structure is straightforward:

```csv
id1,id2,res-diff,mov-flag
```

where 

 - `id1` is the id of the first resource
 - `id1` is the id of the second resource
 - `res-diff` is expected difference result between resource 1 and resource 2
 - `mov-flag` is expected value of the MOV flag (set or not set)