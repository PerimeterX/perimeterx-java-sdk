package com.perimeterx.internals;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

public interface PXValidator {

    boolean verify(PXContext pxContext) throws PXException;

}
