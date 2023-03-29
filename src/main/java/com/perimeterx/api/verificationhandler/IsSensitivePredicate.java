package com.perimeterx.api.verificationhandler;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Predicate;

@FunctionalInterface
public interface IsSensitivePredicate extends Predicate<HttpServletRequest> {
}
