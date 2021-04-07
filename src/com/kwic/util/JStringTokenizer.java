package com.kwic.util;

/**
 * java.util.StringTokenizer 를 개선한 class
 * -------------------------------------------------------------
 * 1. 개선사항
 *	길이가 없는 token을 허용하여 null 값을 얻을 수 있도록 한다.
 *  ArrayList를 통하여 여러개의 delimiter를 사용할 수 있다.
 *
 * @author  Justin Lee (solacer@hanmail.net) , original source from rookey94@hanmail.net
 * @version 1.00, 08/01/2002
 * @since 1.0
 */
//package usrCom;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;
 
@SuppressWarnings("rawtypes")
public class JStringTokenizer implements Enumeration {
    private int currentPosition;
    private int newPosition;
    private int maxPosition;
    private String str;
    private ArrayList delimiters;
    private boolean retDelims;
    private boolean delimsChanged;
    private boolean allowZeroLength;

    public JStringTokenizer(String str, ArrayList delim, boolean returnDelims, boolean allowZeroLength) {
        currentPosition = 0;
        newPosition = -1;
        delimsChanged = false;
        this.str = str;
        maxPosition = str.length();
        delimiters = delim;
        retDelims = returnDelims;
        this.allowZeroLength = allowZeroLength;
    }

    @SuppressWarnings("unchecked")
	public JStringTokenizer(String str, String delim, boolean returnDelims, boolean allowZeroLength) {
        currentPosition = 0;
        newPosition = -1;
        delimsChanged = false;
        this.str = str;
        maxPosition = str.length();
        delimiters = new ArrayList();
        delimiters.add(delim);
        retDelims = returnDelims;
        this.allowZeroLength = allowZeroLength;
    }

    public JStringTokenizer(String str, ArrayList delim, boolean returnDelims) {
        this(str, delim, returnDelims, false);
    }

    public JStringTokenizer(String str, String delim, boolean returnDelims) {
        this(str, delim, returnDelims, false);
    }

    public JStringTokenizer(String str, ArrayList delim) {
        this(str, delim, false, false);
    }

    public JStringTokenizer(String str, String delim) {
        this(str, delim, false, false);
    }

    private String findToken(int position) {
        for (int i = 0; i < delimiters.size(); i++) {
            String delim = (String) delimiters.get(i);
            if (str.startsWith(delim, position)) {
                return delim;
            }
        }
        return null;
    }

    private int skipDelimiters(int startPos) {
        if (delimiters == null) {
            throw new NullPointerException();
        }

        String delim;
        int position = startPos;
        while (!retDelims && position < maxPosition) {
            if ( (delim = findToken(position)) == null) {
                break;
            }
            position += delim.length();
            if (allowZeroLength) {
                break;
            }
        }
        return position;
    }

    private int scanToken(int startPos) {
        String delim;
        int position = startPos;
        while (position < maxPosition) {
            if ( (delim = findToken(position)) != null) {
                break;
            }
            position++;
        }
        if (retDelims && (startPos == position)) {
            if ( (delim = findToken(position)) != null) {
                position += delim.length();
            }
        }
        return position;
    }

    public boolean hasMoreTokens() {
        newPosition = skipDelimiters(currentPosition);
        return (newPosition < maxPosition);
    }

    public String nextToken() {
        currentPosition = (newPosition >= 0 && !delimsChanged) ? newPosition : skipDelimiters(currentPosition);

        delimsChanged = false;
        newPosition = -1;

        if (currentPosition >= maxPosition) {
            throw new NoSuchElementException();
        }
        int start = currentPosition;
        currentPosition = scanToken(currentPosition);
        return str.substring(start, currentPosition);
    }

    public String nextToken(ArrayList delim) {
        delimiters = delim;
        delimsChanged = true;

        return nextToken();
    }

    public boolean hasMoreElements() {
        return hasMoreTokens();
    }

    public Object nextElement() {
        return nextToken();
    }

    public int countTokens() {
        int count = 0;
        int currpos = currentPosition;
        while (currpos < maxPosition) {
            currpos = skipDelimiters(currpos);
            if (currpos >= maxPosition) {
                break;
            }
            currpos = scanToken(currpos);
            count++;
        }
        return count;
    }
}
