/**
 * Copyright 2007 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hama;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.io.Cell;
import org.apache.hadoop.hbase.io.HbaseMapWritable;
import org.apache.hadoop.hbase.io.RowResult;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hama.io.VectorWritable;
import org.apache.log4j.Logger;

public class Vector extends VectorWritable implements VectorInterface {
  static final Logger LOG = Logger.getLogger(Vector.class);

  public Vector() {
    this(null, new HbaseMapWritable<byte[], Cell>());
  }

  public Vector(final byte[] row, final HbaseMapWritable<byte[], Cell> m) {
    this.row = row;
    this.cells = m;
  }

  public Vector(int row, RowResult rowResult) {
    this.cells = new HbaseMapWritable<byte[], Cell>();
    this.row = intToBytes(row);
    for (Map.Entry<byte[], Cell> f : rowResult.entrySet()) {
      this.cells.put(f.getKey(), f.getValue());
    }
  }

  public void add(int index, double value) {
    // TODO Auto-generated method stub

  }

  public Vector add(double alpha, Vector v) {
    // TODO Auto-generated method stub
    return null;
  }

  public Vector add(Vector v2) {
    HbaseMapWritable<byte[], Cell> trunk = new HbaseMapWritable<byte[], Cell>();
    for (int i = 0; i < this.size(); i++) {
      double value = (this.get(i) + v2.get(i));
      Cell cValue = new Cell(String.valueOf(value), System.currentTimeMillis());
      trunk.put(Bytes.toBytes("column:" + i), cValue);
    }

    return new Vector(row, trunk);
  }

  public double dot(Vector v) {
    double cosine = 0.0;
    double q_i, d_i;
    for (int i = 0; i < Math.min(this.size(), v.size()); i++) {
      q_i = v.get(i);
      d_i = this.get(i);
      cosine += q_i * d_i;
    }
    return cosine / (this.getNorm2() * v.getNorm2());
  }

  public Vector scale(double alpha) {
    Set<byte[]> keySet = cells.keySet();
    Iterator<byte[]> it = keySet.iterator();

    while (it.hasNext()) {
      byte[] key = it.next();
      double oValue = bytesToDouble(get(key).getValue());
      double nValue = oValue * alpha;
      Cell cValue = new Cell(String.valueOf(nValue), System.currentTimeMillis());
      cells.put(key, cValue);
    }

    return this;
  }

  public double get(int index) {
    return bytesToDouble(this.cells.get(getColumnIndex(index)).getValue());
  }

  public double norm(Norm type) {
    if (type == Norm.One)
      return getNorm1();
    else if (type == Norm.Two)
      return getNorm2();
    else if (type == Norm.TwoRobust)
      return getNorm2Robust();
    else
      return getNormInf();
  }

  public void set(int index, double value) {
    // TODO Auto-generated method stub

  }

  public Vector set(Vector v) {
    // TODO Auto-generated method stub
    return null;
  }

  public double getNorm1() {
    double sum = 0.0;

    Set<byte[]> keySet = cells.keySet();
    Iterator<byte[]> it = keySet.iterator();

    while (it.hasNext()) {
      sum += bytesToDouble(get(it.next()).getValue());
    }

    return sum;
  }

  public double getNorm2() {
    double square_sum = 0.0;

    Set<byte[]> keySet = cells.keySet();
    Iterator<byte[]> it = keySet.iterator();

    while (it.hasNext()) {
      double value = bytesToDouble(get(it.next()).getValue());
      square_sum += value * value;
    }

    return Math.sqrt(square_sum);
  }

  public double getNorm2Robust() {
    // TODO Auto-generated method stub
    return 0;
  }

  public double getNormInf() {
    // TODO Auto-generated method stub
    return 0;
  }
}
