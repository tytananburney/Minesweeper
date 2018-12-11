import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Tile extends JButton
{

   private int row;
   private int column;
   private int adjacentMines;
   private boolean isMine;
   private boolean marked;
   
   /* Constructor 
      A tile has three possible states: revealed, marked, hidden */
   public Tile(int r, int c)
   {
      row = r;
      column = c;   
      
      adjacentMines = 0;
      isMine = false;
      marked = false;
      
      setFont(new Font("Ariel", Font.BOLD, 14));
      setMargin(new Insets(1,1,1,1));
      setBackground(Color.gray);
      setForeground(Color.gray);
      
   }
   
   /* Getter Methods */
   public int getRow()
   {
      return row;
   }
   
   public int getCol()
   {
      return column;
   }
   
   public int getAdjacent() 
   {
      return adjacentMines;
   }
   
   public boolean isaMine() 
   {   
      return isMine;    
   }
   
   public boolean isMarked()
   {
      return marked;
   }
   
   /* Setter Methods */
   public void setMine(boolean b)
   {
      isMine = b;
      if(b == true)
      {
         adjacentMines = -1;
      }
      else
      {
         adjacentMines = 0; // increment past non-zero only allowed with addAdjacent() method
      }
   }
   
   public void addAdjacent()
   {
      adjacentMines++;
   }
   
   /* Exposes the contents of this tile */
   public void reveal()
   {
      if(isMine) 
      {
         setBackground(Color.red);
         setText("o");
      }
      else if(adjacentMines == 0)
      {
         setBackground(Color.white);
         setText("");
      }
      else 
      {
         setBackground(Color.white);
         setText(adjacentMines+"");
      }
      setEnabled(false);
      marked = false;
   }
   
   /* Returns this Tile to its default visibility state */
   public void hide()
   {
      setForeground(Color.gray);
      setBackground(Color.gray);
      setText("");
      setEnabled(true);
      marked = false;
   }
   
   /* Sets the appearance of this tile to a "marked" intermediate state between hidden and revealed */
   public void mark()
   {
      setBackground(Color.yellow);
      setForeground(Color.red);
      setText("!");
      marked = true;
   }
}