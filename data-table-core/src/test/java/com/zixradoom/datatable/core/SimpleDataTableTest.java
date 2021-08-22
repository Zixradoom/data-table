package com.zixradoom.datatable.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class SimpleDataTableTest {
  
  @Test
  void constructor1GoodArgsTest() {
    SimpleDataTable < DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory () );
    assertEquals ( 0, table.getEntryCount (), "entry count is not zero" );
    assertEquals ( 12, table.getEntrySize (), "entry size is not 12" );
    assertEquals ( 0, table.getEncodedSize (), "encoded size is not zero" );
  }
  
  @Test
  void constructor1NullFactoryTest() {
	  
    NullPointerException ext = assertThrows ( NullPointerException.class, () -> {
      new SimpleDataTable<> ( null );
    });
    
    assertEquals ( "factory is null", ext.getMessage () );
  }
  
  @Test
  void constructor2GoodArgsTest () {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    SimpleDataTable < DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, 0 );
    assertEquals ( 0, table.getEntryCount (), "entry count is not zero" );
    assertEquals ( 15, table.getEntrySize (), "entry size is not 15" );
    assertEquals ( 0, table.getEncodedSize (), "encoded size is not zero" );
  }
  
  @Test
  void constructor2NullFactoryTest() {
	  
    NullPointerException ext = assertThrows ( NullPointerException.class, () -> {
      new SimpleDataTable<> ( null, ByteBuffer.allocate ( 0 ), 0, 0 );
    });
    
    assertEquals ( "factory is null", ext.getMessage () );
  }
  
  @Test
  void constructor2NullBufferTest() {
	  
    NullPointerException ext = assertThrows ( NullPointerException.class, () -> {
      new SimpleDataTable<> ( new DummyEntry.Factory (), null, 0, 0 );
    });
    
    assertEquals ( "table is null", ext.getMessage () );
  }
  
  @Test
  void constructor2NegativeEntrySizeTest() {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    IllegalArgumentException ext = assertThrows ( IllegalArgumentException.class, () -> {
      new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, -1, 0 );
    });
    
    assertEquals ( "entrySize -1 < 1", ext.getMessage () );
  }
  
  @Test
  void constructor2ZeroEntrySizeTest() {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    IllegalArgumentException ext = assertThrows ( IllegalArgumentException.class, () -> {
      new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, 0, 0 );
    });
    
    assertEquals ( "entrySize 0 < 1", ext.getMessage () );
  }
  
  @Test
  void constructor2NegativeEntryCountTest() {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    IllegalArgumentException ext = assertThrows ( IllegalArgumentException.class, () -> {
      new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, -1 );
    });
    
    assertEquals ( "entryCount -1 < 0", ext.getMessage () );
  }
  
  @Test
  void constructor2BufferTooSmallTest() {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate (0);
    
    IllegalArgumentException ext = assertThrows ( IllegalArgumentException.class, () -> {
      new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, 1 );
    });
    
    assertEquals ( "table capacity too small 0 < 15 (size:15,count:1)", ext.getMessage () );
  }
  
  @Test
  void constructor2EntryCountTest () {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    DataTable< DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, 1 );
    
    assertEquals ( 1, table.getEntryCount () );
    assertEquals ( 1, table.getEntries ().size () );
  }
  
  @Test
  void entriesReturnsUnmodifalbleCollectionsTest () {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    DataTable< DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, 1 );
    
    assertThrows ( UnsupportedOperationException.class, () -> {
      table.getEntries ().add ( null );
    });
  }
  
  @Test
  void constructor2EntryCountEncodedSizeTest () {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    DataTable< DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, 1 );
    
    assertEquals ( 15, table.getEncodedSize () );
  }
  
  @Test
  void entryTheSameTest () {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    DataTable< DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, 1 );
    DummyEntry de1 = table.newEntry ();
    table.newEntry ();
    assertSame ( de1, table.getEntry ( 1 ) );
  }
  
  @Test
  void entryIndexTest () {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    DataTable< DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, 1 );
    table.newEntry ();
    table.newEntry ();
    List< ? extends DataTable.Entry > entries = table.getEntries ();
    for ( int index = 0; index < entries.size (); index++ ) {
      assertSame ( index, entries.get ( index ).getIndex () );
    }
  }
  
  @Test
  void dataTableEncodedDataFormatTest () {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    DataTable< DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize, 1 );
    table.newEntry ();
    table.newEntry ();
    
    ByteBuffer dat = table.getEncoded ();
    assertEquals ( 45, dat.position () );
    assertEquals ( 45, dat.limit () );
  }
  
  @Test
  void entryDataFormatTest () {
    final int entrySize = DummyEntry.OFFSET_END + 3;
    final ByteBuffer buffer = ByteBuffer.allocate ( entrySize * 12 );
    
    DataTable< DummyEntry > table = new SimpleDataTable<> ( new DummyEntry.Factory (), buffer, entrySize );
    DummyEntry de1 = table.newEntry ();
    de1.setF2 ( 0xFFA );
    
    ByteBuffer dat = table.getEncoded ();
    long value = dat.getLong( 4 );
    assertEquals ( 0xFFA, value );
  }
  
  static class DummyEntry extends AbstractDataTableEntry {

	public static final int OFFSET_F1 = 0;
    public static final int OFFSET_F2 = OFFSET_F1 + Integer.BYTES;
    public static final int OFFSET_END = OFFSET_F2 + Long.BYTES;
    
    protected DummyEntry ( int index, Supplier< ByteBuffer > bufferGetter, IntSupplier entrySizeGetter ) {
      super ( index, bufferGetter, entrySizeGetter );
    }
    
    public int getF1 () {
      return buffer ().getInt ( start () + OFFSET_F1 );
    }
    
    public void setF1 ( int f1 ) {
      buffer ().putInt ( start () + OFFSET_F1, f1 );
    }
    
    public long getF2 () {
      return buffer ().getLong ( start () + OFFSET_F2 );
    }
    
    public void setF2 ( long f2 ) {
      buffer ().putLong ( start () + OFFSET_F2, f2 );
    }
    
    static class Factory implements DataTable.Factory < DummyEntry > {

      @Override
      public DummyEntry createEntry(int index, Supplier<ByteBuffer> bufferGetter, IntSupplier entrySizeGetter) {
        return new DummyEntry ( index, bufferGetter, entrySizeGetter );
      }

      @Override
      public int getEntrySize() {
        return DummyEntry.OFFSET_END;
      }
    
    }
  }
}
