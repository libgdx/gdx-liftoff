/*
 * Copyright (c) 2022-2023 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package gdx.liftoff;

import java.util.Comparator;

/**
 * A Comparator that can sort Strings, StringBuilders, and other CharSequences by "natural text order," also called
 * Alphanum sort order. This "unofficial natural order" for text treats groups of digits as one number, and sorts using that one
 * numerical value instead of the lexicographic order that is more commonly used in programming languages. This is based on
 * <a href="https://github.com/gpanther/java-nat-sort">Grey Panther's code</a>, extending it slightly so that it sorts all
 * upper-case letters before any lower-case letters, in any language with case. You don't construct a new one of these; instead,
 * use {@link #CASE_SENSITIVE} or {@link #CASE_INSENSITIVE} to get a predefined immutable instance.
 */
public class NaturalTextComparator implements Comparator<CharSequence> {
	public static final NaturalTextComparator INSTANCE = new NaturalTextComparator(true);
	public static final NaturalTextComparator CASE_INSENSITIVE = new NaturalTextComparator(false);
	public static final NaturalTextComparator CASE_SENSITIVE = INSTANCE;

	private final boolean caseSensitive;

	private NaturalTextComparator() {
		this(true);
	}

	private NaturalTextComparator(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	@Override
	public int compare (CharSequence sequence1, CharSequence sequence2) {
		int len1 = sequence1.length(), len2 = sequence2.length();
		int idx1 = 0, idx2 = 0;

		while (idx1 < len1 && idx2 < len2) {
			char c1 = sequence1.charAt(idx1++);
			char c2 = sequence2.charAt(idx2++);

			boolean isDigit1 = isDigit(c1);
			boolean isDigit2 = isDigit(c2);

			if (isDigit1 && !isDigit2) {
				return -1;
			} else if (!isDigit1 && isDigit2) {
				return 1;
			} else if (!isDigit1) {
				int c = compareChars(c1, c2);
				if (c != 0) {
					return c;
				}
			} else {
				long num1 = parse(c1);
				while (idx1 < len1) {
					char digit = sequence1.charAt(idx1++);
					if (isDigit(digit)) {
						num1 = num1 * 10 + parse(digit);
					} else if (digit != ',') {
						idx1--;
						break;
					}
				}

				long num2 = parse(c2);
				while (idx2 < len2) {
					char digit = sequence2.charAt(idx2++);
					if (isDigit(digit)) {
						num2 = num2 * 10 + parse(digit);
					} else if (digit != ',') {
						idx2--;
						break;
					}
				}

				if (num1 != num2) {
					return compareUnsigned(num1, num2);
				}
			}
		}

		if (idx1 < len1) {
			return 1;
		} else if (idx2 < len2) {
			return -1;
		} else {
			return 0;
		}
	}

	private int compareChars (char c1, char c2) {
		char u1 = Character.toUpperCase(c1);
		char u2 = Character.toUpperCase(c2);
		if (caseSensitive) {
			if ((u1 == c1) && (u2 != c2)) { // c1 is a symbol or capital letter, and c2 is not
				return Integer.MIN_VALUE;
			}
			if ((u1 != c1) && (u2 == c2)) {
				return Integer.MAX_VALUE;
			}
			return u1 - u2;
		} else {
			return u1 - u2;
		}
	}

	private static int compareUnsigned (long num1, long num2) {
		return Long.compare(num1 + Long.MIN_VALUE, num2 + Long.MIN_VALUE);
	}

	private static long parse (char c1) {
		return c1 - '0';
	}

	private static boolean isDigit (char c) {
		return '0' <= c & c <= '9';
	}
}
