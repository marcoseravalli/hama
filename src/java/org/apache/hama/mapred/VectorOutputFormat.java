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
package org.apache.hama.mapred;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.BatchUpdate;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.FileAlreadyExistsException;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.InvalidJobConfException;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.apache.hama.io.VectorUpdate;

public class VectorOutputFormat extends
    FileOutputFormat<IntWritable, VectorUpdate> {

  /** JobConf parameter that specifies the output table */
  public static final String OUTPUT_TABLE = "hama.mapred.output";
  private final static Log LOG = LogFactory.getLog(VectorOutputFormat.class);

  /**
   * Convert Reduce output (key, value) to (IntWritable, VectorUpdate)
   * and write to an HBase table
   */
  protected static class TableRecordWriter implements
      RecordWriter<IntWritable, VectorUpdate> {
    private HTable m_table;

    /**
     * Instantiate a TableRecordWriter with the HBase HClient for writing.
     * 
     * @param table
     */
    public TableRecordWriter(HTable table) {
      m_table = table;
    }

    public void close(@SuppressWarnings("unused")
    Reporter reporter) throws IOException {
      m_table.flushCommits();
    }

    /** {@inheritDoc} */
    public void write(IntWritable key, VectorUpdate value) throws IOException {
      m_table.commit(new BatchUpdate(value.getBatchUpdate()));
    }
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public RecordWriter getRecordWriter(FileSystem ignored, JobConf job,
      String name, Progressable progress) throws IOException {

    // expecting exactly one path

    String tableName = job.get(OUTPUT_TABLE);
    HTable table = null;
    try {
      table = new HTable(new HBaseConfiguration(job), tableName);
    } catch (IOException e) {
      LOG.error(e);
      throw e;
    }
    return new TableRecordWriter(table);
  }

  /** {@inheritDoc} */
  @Override
  public void checkOutputSpecs(FileSystem ignored, JobConf job)
      throws FileAlreadyExistsException, InvalidJobConfException, IOException {

    String tableName = job.get(OUTPUT_TABLE);
    if (tableName == null) {
      throw new IOException("Must specify table name");
    }
  }
}
