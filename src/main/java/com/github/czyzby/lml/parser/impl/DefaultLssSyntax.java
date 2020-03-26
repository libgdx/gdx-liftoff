package com.github.czyzby.lml.parser.impl;

import com.github.czyzby.lml.parser.LssSyntax;

/** Uses default LML style sheet syntax markers.
 *
 * @author MJ */
public class DefaultLssSyntax implements LssSyntax {
    @Override
    public char getInheritanceMarker() {
        return '.';
    }

    @Override
    public char getBlockOpening() {
        return '{';
    }

    @Override
    public char getBlockClosing() {
        return '}';
    }

    @Override
    public char getSeparator() {
        return ':';
    }

    @Override
    public char getLineEnd() {
        return ';';
    }

    @Override
    public char getTagSeparator() {
        return ',';
    }

    @Override
    public char getCommentMarker() {
        return '/';
    }

    @Override
    public char getSecondaryCommentMarker() {
        return '*';
    }
}
