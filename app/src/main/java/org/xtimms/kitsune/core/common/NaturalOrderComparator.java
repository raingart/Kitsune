/*
 NaturalOrderComparator.java -- Perform 'natural order' comparisons of strings in Java.
 Copyright (C) 2003 by Pierre-Luc Paour <natorder@paour.com>

 Based on the C version by Martin Pool, of which this is more or less a straight conversion.
 Copyright (C) 2000 by Martin Pool <mbp@humbug.org.au>

 This software is provided 'as-is', without any express or implied
 warranty.  In no event will the authors be held liable for any damages
 arising from the use of this software.

 Permission is granted to anyone to use this software for any purpose,
 including commercial applications, and to alter it and redistribute it
 freely, subject to the following restrictions:

 1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
 2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
 3. This notice may not be removed or altered from any source distribution.
 */
package org.xtimms.kitsune.core.common;

import java.util.Comparator;

public class NaturalOrderComparator<T> implements Comparator<T> {

	private int compareRight(String a, String b) {
		int bias = 0, ia = 0, ib = 0;

		// The longest run of digits wins. That aside, the greatest
		// value wins, but we can't know that it will until we've scanned
		// both numbers to know that they have the same magnitude, so we
		// remember it in BIAS.
		for (; ; ia++, ib++) {
			char ca = charAt(a, ia);
			char cb = charAt(b, ib);

			if (!Character.isDigit(ca) && !Character.isDigit(cb)) {
				return bias;
			}
			if (!Character.isDigit(ca)) {
				return -1;
			}
			if (!Character.isDigit(cb)) {
				return +1;
			}
			if (ca == 0 && cb == 0) {
				return bias;
			}

			if (bias == 0) {
				if (ca < cb) {
					bias = -1;
				} else if (ca > cb) {
					bias = +1;
				}
			}
		}
	}

	public int compare(T o1, T o2) {
		final String a = objectToString(o1);
		final String b = objectToString(o2);

		int ia = 0, ib = 0;
		int nza, nzb;
		char ca, cb;

		while (true) {
			// Only count the number of zeroes leading the last number compared
			nza = nzb = 0;

			ca = charAt(a, ia);
			cb = charAt(b, ib);

			// skip over leading spaces or zeros
			while (Character.isSpaceChar(ca) || ca == '0') {
				if (ca == '0') {
					nza++;
				} else {
					// Only count consecutive zeroes
					nza = 0;
				}

				ca = charAt(a, ++ia);
			}

			while (Character.isSpaceChar(cb) || cb == '0') {
				if (cb == '0') {
					nzb++;
				} else {
					// Only count consecutive zeroes
					nzb = 0;
				}

				cb = charAt(b, ++ib);
			}

			// Process run of digits
			if (Character.isDigit(ca) && Character.isDigit(cb)) {
				int bias = compareRight(a.substring(ia), b.substring(ib));
				if (bias != 0) {
					return bias;
				}
			}

			if (ca == 0 && cb == 0) {
				// The strings compare the same. Perhaps the caller
				// will want to call strcmp to break the tie.
				return nza - nzb;
			}
			if (ca < cb) {
				return -1;
			}
			if (ca > cb) {
				return +1;
			}

			++ia;
			++ib;
		}
	}

	protected String objectToString(T obj) {
		return obj.toString();
	}

	private static char charAt(String s, int i) {
		return i >= s.length() ? 0 : s.charAt(i);
	}
}