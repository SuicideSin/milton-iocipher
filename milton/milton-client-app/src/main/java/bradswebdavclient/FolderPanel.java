/*
 * FolderPanel.java
 *
 * Created on February 13, 2008, 5:41 PM
 */
package bradswebdavclient;

import bradswebdavclient.util.BareBonesBrowserLaunch;
import com.ettrema.httpclient.File;
import com.ettrema.httpclient.Folder;
import com.ettrema.httpclient.FolderListener;
import com.ettrema.httpclient.Resource;
import com.ettrema.httpclient.ResourceListener;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import net.iharder.dnd.FileDrop;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author  bradm
 */
public class FolderPanel extends javax.swing.JPanel implements Addressable, Unloadable {

    private static final long serialVersionUID = 1L;
    FolderModel model;
    TableResourceTransferHandler tableTransferHandler;

    /** Creates new form FolderPanel */
    public FolderPanel( final Folder folder ) throws IOException {
        System.out.println( "FolderPanel: create new" );
        initComponents();
        model = new FolderModel( folder );
        table.setModel( model );
        TableColumn col = table.getColumnModel().getColumn( 0 );
        col.setCellRenderer( new MyTableCellRenderer() );

        new FileDrop( this, new FileDrop.Listener() {

            public void filesDropped( java.io.File[] files ) {
                doFilesDropped( files );
            }
        } );

        tableTransferHandler = new TableResourceTransferHandler( table, new TableDroppable() );
        //TableTransferHandler.initInstance(table, model);
    }

    private void doFilesDropped( final java.io.File[] files ) {
        Application app = BradsWebdavClientApp.getApplication();
        Task task = new UploadTask( this, BradsWebdavClientApp.getApplication(), files, model.folder );
        BradsWebdavClientApp.getApplication().getContext().getTaskService().execute( task );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    javax.swing.JScrollPane moduleTableScroll = new javax.swing.JScrollPane();
    table = new javax.swing.JTable();

    setName("Form"); // NOI18N
    setLayout(new java.awt.BorderLayout());

    moduleTableScroll.setMinimumSize(new java.awt.Dimension(200, 50));
    moduleTableScroll.setName("moduleTableScroll"); // NOI18N
    moduleTableScroll.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(java.awt.event.KeyEvent evt) {
        moduleTableScrollKeyTyped(evt);
      }
    });

    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(bradswebdavclient.BradsWebdavClientApp.class).getContext().getResourceMap(FolderPanel.class);
    table.setForeground(resourceMap.getColor("table.foreground")); // NOI18N
    table.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Name", "Display Name", "Type", "Created Date", "Modified Date"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    table.setGridColor(resourceMap.getColor("table.gridColor")); // NOI18N
    table.setName("table"); // NOI18N
    table.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(java.awt.event.KeyEvent evt) {
        tableKeyTyped(evt);
      }
    });
    table.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        tableMouseClicked(evt);
      }
    });
    moduleTableScroll.setViewportView(table);

    add(moduleTableScroll, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  private void moduleTableScrollKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_moduleTableScrollKeyTyped
  }//GEN-LAST:event_moduleTableScrollKeyTyped

  private void tableKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyTyped
      List<Resource> toDelete = new ArrayList();
      for( int rowNum : table.getSelectedRows() ) {
          Resource r = model.getResource( rowNum );
          if( r != null ) toDelete.add( r );
      }

      if( toDelete.size() == 0 ) return;

      ResourceUtils.doDelete( this, (int) evt.getKeyChar(), toDelete );
}//GEN-LAST:event_tableKeyTyped

  private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
      if( evt.getClickCount() == 2 ) {
          int row = table.getSelectedRow();
          Resource r = model.getResource( row );
          if( r instanceof Folder ) {
              try {
                  App.current().view.showDetails( new FolderPanel( (Folder) r ) );
              } catch( IOException ex ) {
                  throw new RuntimeException( ex );
              }
          } else {
              File f = (File) r;
              if( ( f.contentType != null && f.contentType.equals( "text/html" ) ) || f.name.endsWith( "html" ) ) {
                  BareBonesBrowserLaunch.openURL( r.href() );
              } else if( ( f.contentType != null && f.contentType.contains( "image" ) ) || f.name.endsWith( "jpg" ) ) {
                  BareBonesBrowserLaunch.openURL( r.href() );
              } else if( f.contentType != null && f.contentType.contains( "text" ) ) {
                  openTextEditor( f );
              } else {
                  java.io.File dest = new java.io.File( "/home/brad/Desktop" ); // TODO
                  java.io.File rFile;
                  try {
                      rFile = r.downloadTo( dest, null );
                  } catch( IOException ex ) {
                      throw new RuntimeException( ex );
                  }
                  String url = "file://" + rFile.getAbsolutePath();
                  BareBonesBrowserLaunch.openURL( url );
              }
          }
      }
  }//GEN-LAST:event_tableMouseClicked

    public void unload() {
        model.unload();
        this.tableTransferHandler.unload();
    }

    public String getHref() {
        return model.folder.href();
    }

    private void openTextEditor( File f ) {
        TextEditorFrame frm = new TextEditorFrame( f );
        frm.setVisible( true );
    }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTable table;
  // End of variables declaration//GEN-END:variables

    class FolderModel extends AbstractTableModel implements FolderListener, ResourceListener, Unloadable {

        private static final long serialVersionUID = 1L;
        final Folder folder;
        List<Resource> children;

        public FolderModel( Folder folder ) throws IOException {
            this.folder = folder;
            this.folder.addListener( (FolderListener) this );
            this.folder.addListener( (ResourceListener) this );
        }

        public void onChanged( Resource r ) {
            if( r == folder ) {
                // do nothing
            } else {
                if( children != null ) {
                    int index = children.indexOf( r );
                    if( index >= 0 ) {
                        fireTableRowsUpdated( index, index );
                    }
                }
            }
        }

        public void onDeleted( Resource r ) {
            System.out.println( "onDeleted: " + r.name );
            if( r == folder ) {
                App.current().view.showDetails( null );
            } else {
                System.out.println( "  child deleted" );
                if( children != null ) {
                    int index = children.indexOf( r );
                    System.out.println( "  child index: " + index );
                    if( index >= 0 ) {
                        children.remove( index );
                        FolderModel.this.fireTableRowsDeleted( index, index );
                    }
                }
            }
        }

        public void onChildAdded( Folder parent, Resource child ) {
            if( parent == folder ) {
                if( children != null ) {
                    int index = children.indexOf( child );
                    child.addListener( this );
                    fireTableRowsInserted( index, index );
                }
            }
        }

        public void onChildRemoved( Folder parent, Resource child ) {
            if( parent == folder ) {
                if( children != null ) {
                    int index = children.indexOf( child );
                    fireTableRowsInserted( index, index );
                }
            }
        }

        public int getRowCount() {
            return children().size();
        }

        Resource getResource( int row ) {
            return children().get( row );
        }

        List<? extends Resource> children()  {
            try {
                if( children != null ) {
                    return children;
                }
                children = (List<Resource>) folder.children();
                Collections.sort( children, new ResourceComparator() );
                for( Resource r : children ) {
                    r.addListener( this );
                }
                return children;
            } catch( IOException ex ) {
                throw new RuntimeException( ex );
            }
        }

        public int getColumnCount() {
            return 5;
        }

        @Override
        public String getColumnName( int column ) {
            switch( column ) {
                case 0:
                    return "Name";
                case 1:
                    return "Display Name";
                case 2:
                    return "Type";
                case 3:
                    return "Size";
                case 4:
                    return "Created";
                case 5:
                    return "Modified";
                default:
                    return "?";
            }

        }

        public Object getValueAt( int rowIndex, int columnIndex ) {
            Resource r = getResource( rowIndex );
            if( r == null ) {
                return null;
            }
            switch( columnIndex ) {
                case 0:
                    return r.name;
                case 1:
                    return r.displayName;
                case 2:
                    if( r instanceof File ) {
                        return ( (File) r ).contentType;
                    } else {
                        return "Folder";
                    }
                case 3:
                    if( r instanceof File ) {
                        return ( (File) r ).contentLength;
                    } else {
                        return "";
                    }
                case 4:
                    return r.getCreatedDate();
                case 5:
                    return r.getModifiedDate();
                case 6:
                    return "?";
                default:
                    return "unknown " + columnIndex;
            }
        }

        List<Resource> getSelectedResources() {
            int[] rows = table.getSelectedRows();
            List<Resource> list = new ArrayList<Resource>();
            for( int rowNum : rows ) {
                Resource r = model.getResource( rowNum );
                list.add( r );
            }
            return list;
        }

        public void unload() {
            this.folder.removeListener( (FolderListener) this );
            this.folder.removeListener( (ResourceListener) this );
        }
    }

    class ResourceComparator implements Comparator<Resource> {

        public int compare( Resource o1, Resource o2 ) {
            if( o1 instanceof Folder ) {
                if( o2 instanceof Folder ) {
                    return o1.name.compareToIgnoreCase( o2.name );
                } else {
                    return -1;
                }
            } else {
                if( o2 instanceof Folder ) {
                    return 1;
                } else {
                    return o1.name.compareTo( o2.name );
                }
            }
        }
    }

    public class MyTableCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex ) {
            JLabel label = (JLabel) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, rowIndex, vColIndex );
            Resource r = model.getResource( rowIndex );
            String iconName;
            if( r instanceof Folder ) {
                iconName = FolderNode.ICON_FOLDER;
            } else {
                iconName = "/s_file.png";
            }
            Icon icon = MyCellRenderer.getIcon( iconName );
            label.setIcon( icon );

            label.setText( value.toString() );

            return this;
        }
    }

    class TableDroppable implements Droppable {

        public boolean acceptCopyDrop( Transferable transferable ) {
            TransferableResourceList list = (TransferableResourceList) transferable;
            for( Resource r : list ) {
                try {
                    r.copyTo( model.folder );
                } catch( IOException ex ) {
                    ex.printStackTrace();
                    break;
                }
            }
            return true;
        }

        public boolean acceptMoveDrop( Transferable transferable ) {
            TransferableResourceList list = (TransferableResourceList) transferable;
            for( Resource r : list ) {
                try {
                    r.moveTo( model.folder );
                } catch( IOException ex ) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog( table, "Failed to move: " + model.folder.name);
                }
            }
            return true;
        }

        public boolean canPerformMove( Transferable transferable ) {
            System.out.println( "canPerformMove: true" );
            return true;
        }

        public boolean canPerformCopy( Transferable transferable ) {
            System.out.println( "canPerformCopy: true" );
            return true;
        }
    }
}
