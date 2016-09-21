/*
 * A free Java implementation of Password Based Key Derivation Function 2 as
 * defined by RFC 2898. Copyright 2007, 2014, Matthias G&auml;rtner
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.perimeterx.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This <b>Password Based Key Derivation Function 2</b> implementation.
 * <hr>
 * Request for Comments: 2898 PKCS #5: Password-Based Cryptography Specification
 * <p>
 * Version 2.0
 * <p>
 * <p>
 * PBKDF2 (P, S, c, dkLen)
 * <p>
 * <p>
 * Options:
 * <ul>
 * <li>PRF underlying pseudorandom function (hLen denotes the length in octets
 * of the pseudorandom function output). PRF is pluggable.</li>
 * </ul>
 * <p>
 * <p>
 * Input:
 * <ul>
 * <li>P password, an octet string</li>
 * <li>S salt, an octet string</li>
 * <li>c iteration count, a positive integer</li>
 * <li>dkLen intended length in octets of the derived key, a positive integer,
 * at most (2^32 - 1) * hLen</li>
 * </ul>
 * <p>
 * <p>
 * Output:
 * <ul>
 * <li>DK derived key, a dkLen-octet string</li>
 * </ul>
 *
 * @author Matthias G&auml;rtner
 * @see <a href="http://tools.ietf.org/html/rfc2898">RFC 2898</a>
 */
public class PBKDF2Engine implements PBKDF2 {
    protected PBKDF2Parameters parameters;

    protected PRF prf;

    /**
     * Constructor for PBKDF2 implementation object. PBKDF2 parameters must be
     * passed later.
     */
    public PBKDF2Engine() {
        this.parameters = null;
        prf = null;
    }

    /**
     * Constructor for PBKDF2 implementation object. PBKDF2 parameters are
     * passed so that this implementation knows iteration count, method to use
     * and String encoding.
     *
     * @param parameters Data holder for iteration count, method to use et cetera.
     */
    public PBKDF2Engine(PBKDF2Parameters parameters) {
        this.parameters = parameters;
        prf = null;
    }

    /**
     * Constructor for PBKDF2 implementation object. PBKDF2 parameters are
     * passed so that this implementation knows iteration count, method to use
     * and String encoding.
     *
     * @param parameters Data holder for iteration count, method to use et cetera.
     * @param prf        Supply customer Pseudo Random Function.
     */
    public PBKDF2Engine(PBKDF2Parameters parameters, PRF prf) {
        this.parameters = parameters;
        this.prf = prf;
    }

    public byte[] deriveKey(String inputPassword) {
        return deriveKey(inputPassword, 0);
    }

    public byte[] deriveKey(String inputPassword, int dkLen) {
        byte P[];
        String charset = parameters.getHashCharset();
        if (inputPassword == null) {
            inputPassword = "";
        }
        try {
            if (charset == null) {
                P = inputPassword.getBytes();
            } else {
                P = inputPassword.getBytes(charset);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        assertPRF(P);
        if (dkLen == 0) {
            dkLen = prf.getHLen();
        }
        return PBKDF2(prf, parameters.getSalt(), parameters.getIterationCount(), dkLen);
    }

    public boolean verifyKey(String inputPassword) {
        byte[] referenceKey = getParameters().getDerivedKey();
        if (referenceKey == null || referenceKey.length == 0) {
            return false;
        }
        byte[] inputKey = deriveKey(inputPassword, referenceKey.length);

        if (inputKey == null || inputKey.length != referenceKey.length) {
            return false;
        }
        for (int i = 0; i < inputKey.length; i++) {
            if (inputKey[i] != referenceKey[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Factory method. Default implementation is (H)MAC-based. To be overridden
     * in derived classes.
     *
     * @param P User-supplied candidate password as array of bytes.
     */
    protected void assertPRF(byte[] P) {
        if (prf == null) {
            prf = new MacBasedPRF(parameters.getHashAlgorithm());
        }
        prf.init(P);
    }

    public PRF getPseudoRandomFunction() {
        return prf;
    }

    public void setPseudoRandomFunction(PRF prf) {
        this.prf = prf;
    }

    /**
     * Core Password Based Key Derivation Function 2.
     *
     * @param prf   Pseudo Random Function (i.e. HmacSHA1)
     * @param S     Salt as array of bytes. <code>null</code> means no salt.
     * @param c     Iteration count (see RFC 2898 4.2)
     * @param dkLen desired length of derived key.
     * @return internal byte array
     * @see <a href="http://tools.ietf.org/html/rfc2898">RFC 2898 5.2</a>
     */
    protected byte[] PBKDF2(PRF prf, byte[] S, int c, int dkLen) {
        if (S == null) {
            S = new byte[0];
        }
        int hLen = prf.getHLen();
        int l = ceil(dkLen, hLen);
        int r = dkLen - (l - 1) * hLen;
        byte T[] = new byte[l * hLen];
        int ti_offset = 0;
        for (int i = 1; i <= l; i++) {
            _F(T, ti_offset, prf, S, c, i);
            ti_offset += hLen;
        }
        if (r < hLen) {
            // Incomplete last block
            byte DK[] = new byte[dkLen];
            System.arraycopy(T, 0, DK, 0, dkLen);
            return DK;
        }
        return T;
    }

    /**
     * Integer division with ceiling function.
     *
     * @param a Numerator
     * @param b Denominator
     * @return ceil(a/b)
     * @see <a href="http://tools.ietf.org/html/rfc2898">RFC 2898 5.2 Step 2.</a>
     */
    protected int ceil(int a, int b) {
        int m = 0;
        if (a % b > 0) {
            m = 1;
        }
        return a / b + m;
    }

    /**
     * Function F.
     *
     * @param dest       Destination byte buffer
     * @param offset     Offset into destination byte buffer
     * @param prf        Pseudo Random Function
     * @param S          Salt as array of bytes
     * @param c          Iteration count
     * @param blockIndex The block index (&gt;= 1).
     * @see <a href="http://tools.ietf.org/html/rfc2898">RFC 2898 5.2 Step 3.</a>
     */
    protected void _F(byte[] dest, int offset, PRF prf, byte[] S, int c, int blockIndex) {
        int hLen = prf.getHLen();
        byte U_r[] = new byte[hLen];

        // U0 = S || INT (i);
        byte U_i[] = new byte[S.length + 4];
        System.arraycopy(S, 0, U_i, 0, S.length);
        INT(U_i, S.length, blockIndex);

        for (int i = 0; i < c; i++) {
            U_i = prf.doFinal(U_i);
            xor(U_r, U_i);
        }
        System.arraycopy(U_r, 0, dest, offset, hLen);
    }

    /**
     * Block-Xor. Xor source bytes into destination byte buffer. Destination
     * buffer must be same length or less than source buffer.
     *
     * @param dest destination byte buffer
     * @param src  source bytes
     */
    protected void xor(byte[] dest, byte[] src) {
        for (int i = 0; i < dest.length; i++) {
            dest[i] ^= src[i];
        }
    }

    /**
     * Four-octet encoding of the integer i, most significant octet first.
     *
     * @param dest   destination byte buffer
     * @param offset zero-based offset into dest
     * @param i      the integer to encode
     * @see <a href="http://tools.ietf.org/html/rfc2898">RFC 2898 5.2 Step 3.</a>
     */
    protected void INT(byte[] dest, int offset, int i) {
        dest[offset] = (byte) (i / (256 * 256 * 256));
        dest[offset + 1] = (byte) (i / (256 * 256));
        dest[offset + 2] = (byte) (i / (256));
        dest[offset + 3] = (byte) (i);
    }

    public PBKDF2Parameters getParameters() {
        return parameters;
    }

    public void setParameters(PBKDF2Parameters parameters) {
        this.parameters = parameters;
    }
}
