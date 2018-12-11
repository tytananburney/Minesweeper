import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.lang.Math;
import java.util.Stack;

class Minesweeper extends JPanel implements ActionListener, MouseListener
{

   public static int N;
   public static int M;
   public static int remainingTiles;
   public static Tile[][] myButtons;
   public static Stack<Tile> pileOfMines;
   
   public static int timerCount = 0;
   public static Timer myTimer;
   public static JLabel messages;
   public static JLabel timerBlock;
   
   public static void main(String[] args) 
   {
      N = 12; // Dimensions of the board (N x N)    
      M = 24; // Number of Mines
      
      myButtons = new Tile[N][N];
      pileOfMines = new Stack<Tile>();
      
      remainingTiles = N*N;
      if(M > remainingTiles-9)
         M = remainingTiles-9; // Ensures first Tile is not a mine nor adjacent to a mine
   
   
      /* Set JFrame options */
      JFrame myFrame = new JFrame("Minesweeper"); 
      myFrame.setSize(500,500);
      myFrame.setLocation(150,150);
      myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      
      /* Create the board */
      Minesweeper myPanel = new Minesweeper();
      myFrame.add(myPanel, BorderLayout.CENTER);  
      
      /* Add a title block to the top of the JFrame */
      JButton titleBlock = new JButton("MINESWEEPER");
      titleBlock.setFont(new Font("Georgia",Font.BOLD,24));
      titleBlock.setBorder(new EmptyBorder(8,0,8,0));
      titleBlock.addActionListener(
         
         new ActionListener() 
         {
            /* Stops the timer, resets messages in infoBlock, clears mines, hides and enables all tiles */
            public void actionPerformed(ActionEvent e) 
            {
               myTimer.stop();
               resetBoard();
            }
         });
         
      myFrame.add(titleBlock, BorderLayout.NORTH);
      
      /* Add in-game messages and timer to the bottom of the JFrame */
      JPanel infoBlock = new JPanel();
      messages = new JLabel("Press any tile to start", SwingConstants.CENTER);
      infoBlock.add(messages);
      timerBlock = new JLabel("00:00", SwingConstants.RIGHT);
      infoBlock.add(timerBlock, BorderLayout.EAST);
      myFrame.add(infoBlock, BorderLayout.SOUTH);
      
      myTimer = new Timer(1000, // Update once every second
         
         new ActionListener() 
         {
            String timerString = "";
            public void actionPerformed(ActionEvent e) 
            {
               timerCount++;
               timerString = String.format("%02d:%02d",timerCount/60, timerCount-60*(timerCount/60));
               timerBlock.setText(timerString);
            }      
         });      
      
      myFrame.setVisible(true);      
   }
   
   public Minesweeper()
   {
      setLayout(new GridLayout(N,N));
      setBorder(new EmptyBorder(0,5,0,5));
      
      /* Create a matrix of buttons representing the board */
      for(int i = 0; i < N; i++)
      {
         for(int j = 0; j < N; j++) 
         {
            myButtons[i][j] = new Tile(i, j);
            myButtons[i][j].addActionListener(this);
            myButtons[i][j].addMouseListener(this);
            add(myButtons[i][j]);
         }
      }
   }
   
   /* Randomly generates M number of x and y coordinates and updates the Tile at that location to have a mine. 
      Updates adjacent tiles that are not already mines to account for the added mine. */ 
   public static void placeMines(int r, int c)
   {         
      while(pileOfMines.size() < M)
      {
         int i = (int)(Math.random() * N);
         int j = (int)(Math.random() * N);
      
         boolean nextToFirstTile = ( i >= r-1 && i <= r+1 ) && ( j >= c-1 && j <= c+1 ); // Don't add mines next to the first Tile selected
         if(!myButtons[i][j].isaMine() && !nextToFirstTile)
         {   
            myButtons[i][j].setMine(true);
            pileOfMines.push(myButtons[i][j]);
            updateNeighbors(i, j);
         }
      }  
   }  
   
   /* Increments the value adjacentMines in Tiles that are adjacent to the coordinates (r,c).
      Checks boundary conditions: 0 <= r < N, 0 <= c < N 
      Only updates adjacent tiles which are not also mines. */
   public static void updateNeighbors(int r, int c) 
   {     
      for(int i = Math.max(r-1, 0); i <= Math.min(r+1, N-1); i++)
      {
         for(int j = Math.max(c-1, 0); j <= Math.min(c+1, N-1); j++)
         {
            if(!myButtons[i][j].isaMine())
            {
               myButtons[i][j].addAdjacent();
            }
         }
      }
   }
   
   /* Reveals the Tile at (r,c) and checks all tiles adjacent to it.
      If those Tiles also are not adjacent to any mines and haven't been revealed yet, 
      reveal that Tile and continue checking tiles adjacent to that.*/
   public void clearTileAndNeighbors(int r, int c)
   {
      myButtons[r][c].reveal();
      remainingTiles--;
      
      if(myButtons[r][c].getAdjacent() == 0)
      {
         for(int i = Math.max(r-1, 0); i <= Math.min(r+1, N-1); i++)
         {
            for(int j = Math.max(c-1, 0); j <= Math.min(c+1, N-1); j++)
            {
               if(myButtons[i][j].isEnabled())
               {
                  clearTileAndNeighbors(i, j);   
               }
            }
         }
      }
   }
   
   /* Goes through the whole board is disables all Tiles. 
      Reveals all mines a user defined color.
      Stops the game timer. */
   public void cleanUp(Color mineColor)
   {
      myTimer.stop();
      for(Tile[] a : myButtons)
      {
         for(Tile b : a)
         {
            if(b.isaMine() && b.isEnabled())
            {
               b.reveal();
               b.setBackground(mineColor); // Override revealed background color for mines based on victory/loss condition
            }
            b.setEnabled(false); // Disable all Tiles even if still hidden
         }
      }
   }          

   /* Resets in-game messages, enabled all Tiles, hides the contents of all Tiles, and removes all Mines from the field
      Executed whenever the title block is pressed */
   public static void resetBoard()
   {
      remainingTiles = N*N;
      
      timerBlock.setText("00:00");
      messages.setText("Press any tile to start");
      messages.setForeground(Color.black);
      
      for(Tile[] a : myButtons)
      {
         for(Tile b : a)
         {
            b.hide();
            b.setMine(false);
            pileOfMines = new Stack<Tile>();
         }
      }  
   }
   
   /* Responds when a Tile is pressed down
      Retrieve the row and column of the Tile that was pressed. 
      Place mines if this is the first Tile revealed to ensure first Tile is not a mine.
      Set the selected tile to its revealed state and reveal other Tiles if this one is not adjacent to any mines.
      Check End-of-Game conditions. */
   public void actionPerformed(ActionEvent e)
   {      
      Tile t = (Tile) e.getSource();
      int r = t.getRow();
      int c = t.getCol();
      
      /* Start the game by placing mines and starting the timer */
      if(!myTimer.isRunning())
      {
         placeMines(r, c);
         timerCount = 0;
         myTimer.start();
         messages.setText("Watch out for mines!");
         messages.setForeground(Color.black);
      }
      
      /* Decide how to reveal tiles based on whether there is an adjacent mine */
      if(t.getAdjacent() == 0)
      {
         clearTileAndNeighbors(r, c);
      } 
      else
      {
         t.reveal(); // Reveal even if the tile is a mine
         remainingTiles--;
      }
      
      /* End of game conditions */
      if(t.isaMine()) // Check loss condition first
      {
         messages.setText("BOOM, YOU LOST! Click title to try again.");
         messages.setForeground(Color.red);
         cleanUp(Color.black);
      }
      else if(remainingTiles == M)
      {
         messages.setText("Congratulations, YOU WIN!");
         messages.setForeground(Color.green);
         cleanUp(Color.green);
      }  
   }  
   
   /* Responds on right-clicks on an unmarked & unrevealed Tile */
   public void mouseEntered(MouseEvent m){}
   public void mouseExited(MouseEvent m){}
   public void mousePressed(MouseEvent m){}
   public void mouseReleased(MouseEvent m){}
   public void mouseClicked(MouseEvent m)
   {
      Tile t = (Tile) m.getSource();
      if(m.getButton() == 3 && !t.isMarked() && t.isEnabled()) // Only mark tiles that have not been revealed
      {
         t.mark();
      }
      else if(m.getButton() == 3 && t.isMarked())
      {
         t.hide();
      }
   }
}


 
