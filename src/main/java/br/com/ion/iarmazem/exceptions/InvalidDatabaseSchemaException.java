package br.com.ion.iarmazem.exceptions;

import br.com.ion.iarmazem.Constants;

public class InvalidDatabaseSchemaException extends RuntimeException {

    public InvalidDatabaseSchemaException() {
        super(Constants.INVALID_SCHEMA_MSG_ERROR);
    }

}
