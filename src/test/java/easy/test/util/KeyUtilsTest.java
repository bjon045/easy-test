////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2012, Suncorp Metway Limited. All rights reserved.
//
// This is unpublished proprietary source code of Suncorp Metway Limited.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
////////////////////////////////////////////////////////////////////////////////
// $Id$
// $Revision$
// $Date$
// $Author$
////////////////////////////////////////////////////////////////////////////////
package easy.test.util;

import org.junit.Test;

import easy.test.util.KeyUtils;

public class KeyUtilsTest {

    @Test
    public void testKey() {
        String result = KeyUtils.getKeySequence("dsfsfd[ENTER]sfds[AA]sadasdsa[ENTER]f");
        System.out.println(result);
    }
}
