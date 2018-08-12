package com.perimeterx.internals;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

public interface PXVerifier {


    boolean verify( PXContext pxContext) throws PXException;

}
