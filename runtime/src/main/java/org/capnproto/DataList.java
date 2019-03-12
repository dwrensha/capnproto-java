// Copyright (c) 2013-2014 Sandstorm Development Group, Inc. and contributors
// Licensed under the MIT License:
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
package org.capnproto;

import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class DataList {

    public static final class Factory extends ListFactory<Builder, Reader> {

        Factory() {
            super(ElementSize.POINTER);
        }

        @Override
        public final Reader constructReader(SegmentDataContainer segment,
                int ptr,
                int elementCount, int step,
                int structDataSize, short structPointerCount,
                int nestingLimit) {
            return new Reader(segment, ptr, elementCount, step, structDataSize, structPointerCount, nestingLimit);
        }

        @Override
        public final Builder constructBuilder(GenericSegmentBuilder segment,
                int ptr,
                int elementCount, int step,
                int structDataSize, short structPointerCount) {
            return new Builder(segment, ptr, elementCount, step, structDataSize, structPointerCount);
        }
    }
    public static final Factory factory = new Factory();

    public static final class Reader extends ListReader implements Collection<Data.Reader> {

        public Reader(SegmentDataContainer segment,
                int ptr,
                int elementCount, int step,
                int structDataSize, short structPointerCount,
                int nestingLimit) {
            super(segment, ptr, elementCount, step, structDataSize, structPointerCount, nestingLimit);
        }

        public Stream<Data.Reader> stream() {
            return StreamSupport.stream(Spliterators.spliterator(this.iterator(), elementCount,
                    Spliterator.SIZED & Spliterator.IMMUTABLE
            ), false);
        }

        public Data.Reader get(int index) {
            return _getPointerElement(Data.factory, index);
        }

        @Override
        public boolean isEmpty() {
            return elementCount==0;
        }

        @Override
        public boolean contains(Object o) {
            return stream().anyMatch(o::equals);
        }

        @Override
        public Object[] toArray() {
            return stream().collect(Collectors.toList()).toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return stream().collect(Collectors.toList()).toArray(a);
        }

        @Override
        public boolean add(Data.Reader e) {
            throw new UnsupportedOperationException("This collection is immutable");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("This collection is immutable");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return stream().collect(Collectors.toList()).containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Data.Reader> c) {
            throw new UnsupportedOperationException("This collection is immutable");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("This collection is immutable");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("This collection is immutable");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("This collection is immutable");
        }


        public final class Iterator implements java.util.Iterator<Data.Reader> {

            public Reader list;
            public int idx = 0;

            public Iterator(Reader list) {
                this.list = list;
            }

            @Override
            public Data.Reader next() {
                return this.list._getPointerElement(Data.factory, idx++);
            }

            @Override
            public boolean hasNext() {
                return idx < list.size();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public java.util.Iterator<Data.Reader> iterator() {
            return new Iterator(this);
        }

        @Override
        public String toString() {
            return stream().map(String::valueOf).collect(Collectors.joining(","));
        }

    }

    public static final class Builder extends ListBuilder implements Iterable<Data.Builder> {

        public Builder(GenericSegmentBuilder segment, int ptr,
                int elementCount, int step,
                int structDataSize, short structPointerCount) {
            super(segment, ptr, elementCount, step, structDataSize, structPointerCount);
        }

        public final Data.Builder get(int index) {
            return _getPointerElement(Data.factory, index);
        }

        public final void set(int index, Data.Reader value) {
            _setPointerElement(Data.factory, index, value);
        }

        public final Reader asReader() {
            return new Reader(this.segment, this.ptr, this.elementCount, this.step,
                    this.structDataSize, this.structPointerCount,
                    java.lang.Integer.MAX_VALUE);
        }

        public final class Iterator implements java.util.Iterator<Data.Builder> {

            public Builder list;
            public int idx = 0;

            public Iterator(Builder list) {
                this.list = list;
            }

            @Override
            public Data.Builder next() {
                return this.list._getPointerElement(Data.factory, idx++);
            }

            @Override
            public boolean hasNext() {
                return this.idx < this.list.size();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public java.util.Iterator<Data.Builder> iterator() {
            return new Iterator(this);
        }

    }

}
