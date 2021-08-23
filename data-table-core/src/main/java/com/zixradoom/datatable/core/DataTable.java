
package com.zixradoom.datatable.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class DataTable < E extends DataTable.Entry > {
  
  public static final int DEFAULT_ENTRY_MULTIPLIER = 32;
  
  protected ByteBuffer table;
  protected final int entrySize;
  protected int entryCount;
  protected final Factory < E > factory;
  protected final List < E > entries;
  
  protected DataTable ( Factory < E > factory ) {
    this (
      Objects.requireNonNull ( factory, "factory is null" ), 
      ByteBuffer.allocate ( factory.getEntrySize () * DEFAULT_ENTRY_MULTIPLIER ),
      factory.getEntrySize (), 
      0 );
  }
  
  protected DataTable ( Factory < E > factory, ByteBuffer table, int entrySize ) {
    this ( factory, table, entrySize, 0 );
  }
  
  protected DataTable ( Factory < E > factory, ByteBuffer table, int entrySize, int entryCount ) {
    this.entrySize = entrySize;
    this.entryCount = entryCount;
    this.table = Objects.requireNonNull ( table, "table is null" );
    this.factory = Objects.requireNonNull ( factory, "factory is null" );
    
    if ( entrySize < 1 ) {
      throw new IllegalArgumentException ( String.format ( "entrySize %d < 1", entrySize ) );
    }
    
    if ( entryCount < 0 ) {
      throw new IllegalArgumentException ( String.format ( "entryCount %d < 0", entryCount ) );
    }
    
    if ( table.capacity() < entrySize * entryCount ) {
      throw new IllegalArgumentException ( String.format ( "table capacity too small %d < %d (size:%d,count:%d)", table.capacity(), entrySize * entryCount, entrySize, entryCount ) );
    }
    
    this.entries = new ArrayList <> ();
    for ( int index = 0; index < this.entryCount; index++ ) {
      E entry = factory.createEntry ( index, this::buffer, this::getEntrySize );
      entries.add ( entry );
    }
    
    updateBufferPositons ();
  }
  
  public E newEntry () {
    int index = entryCount;
    entryCount++;
    
    E entry = factory.createEntry ( index, this::buffer, this::getEntrySize );
    entries.add ( entry );
    
    updateBufferPositons();
    
    return entry;
  }
  
  public int getEntrySize () {
    return entrySize;
  }
  
  public int getEntryCount () {
    return entryCount;
  }
  
  public ByteBuffer getEncoded () {
    return table.duplicate ().asReadOnlyBuffer ();
  }
  
  public int getEncodedSize () {
    return table.limit ();
  }
  
  public List < E > getEntries () {
    return Collections.unmodifiableList ( entries );
  }
  
  public E getEntry ( int index ) {
    return entries.get ( index );
  }
  
  private void updateBufferPositons () {
    table.limit ( entrySize * entryCount );
    table.position ( table.limit () );
  }
  
  private ByteBuffer buffer () {
    return table;
  }
  
  public interface Entry {
    int getIndex ();
    int getEntrySize ();
  }
  
  public interface Factory < S extends Entry > {
    S createEntry ( int index, Supplier < ByteBuffer > bufferGetter, IntSupplier entrySizeGetter );
    int getEntrySize ();
  }
}
