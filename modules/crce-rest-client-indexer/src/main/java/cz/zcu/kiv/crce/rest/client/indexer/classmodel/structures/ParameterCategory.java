package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

/**
 * Created by ghessova on 12.03.2018.
 */
public enum ParameterCategory {

    QUERY, // www.example.com/context/resource?queryParam=paramValue
    MATRIX, // www.example.com/context/resource;matrixParam=paramValue
    PATH, // www.example.com/context/resource/pathParamValue
    FORM, // in request body: formParamName=formParamValue
    BODY, // request body
    HEADER, // request/response header
    COOKIE; // request/response cookie (represented by Cookie and Set-Cookie headers)
}
