package com.zixradoom.datatable.core;

import java.nio.ByteBuffer;

public class SimpleDataTable < G extends DataTable.Entry > extends DataTable < G >  {

  protected SimpleDataTable ( DataTable.Factory< G > factory ) {
    super ( factory );
  }

  protected SimpleDataTable ( DataTable.Factory< G > factory, ByteBuffer table, int entrySize ) {
    super ( factory, table, entrySize );
  }
  
  protected SimpleDataTable ( DataTable.Factory< G > factory, ByteBuffer table, int entrySize, int entryCount ) {
    super ( factory, table, entrySize, entryCount );
  }
}
