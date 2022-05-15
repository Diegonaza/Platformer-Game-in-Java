/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platformergame;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Alisson, Diego
 * @comment Alisson, Diego
 */
public class Citizen extends GameObject {
    
    GamePanel panel;
    double movementSpeed = 0;
   
    int startX;
    int startY;
    int camPrevY;
    int camPrevX;
    int spriteIndex = 1;
    int animPlayRate = 0;
    int spriteSheetIndex, sheetLenght;
    Rectangle hitBox;
    
   BufferedImage[] sprites = new BufferedImage[6];
    CharacterState cState;
    myThread cdThread ;
    ColourType colour;
    
      enum Direction{
    Left(),Right();
    }
      //enum for indication of direction for character movement and animation sprite selection.
      
    Direction direction;
    boolean faceLeft,faceRight;
    
    BufferedImage characterSprite;
    Player player;
    int aTimer;

    public Citizen(int x, int y, GamePanel gp) {
         super(x, y);
         
        cState = cState.Walking;
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.xSpeed = 1;
        this.panel = gp;
        this.camPrevY = panel.cameraY;
        this.camPrevX = panel.cameraX;
        width = 32;
        height = 64;
        hitBox = new Rectangle(x,y,width,height);
        
        this.player = (Player) this.panel.handler.object.get(0);
        direction = direction.Right;
        colour = colour.red;
        cdThread = new myThread(player);
        
        ImportImage();
             
    }
    //instanciation of the constructor, to be used in GamePanel's instanciation on the game environment.
    
   

    @Override
    public void tick() {
        
        
       //Character States  
       
      if(xSpeed!=0){
          cState = cState.Walking;
          //when speed on axis X is not zero, this character will be in position walking
      }
      
      if(ySpeed != 0){
          cState = cState.Jumping;
      }// Jumping state overrides the walking state as it comes after it, 
       // When the speed on axis Y is different to Zero, the character will be in Jumping State
      
      if(xSpeed >0){
          direction = direction.Right;
          //This sets up the enum state where the character is facing, if positive speed, the right position is selected.      }
      
      if(xSpeed <0){
          direction = direction.Left;
          //This sets up the enum state where the character is facing, if negative speed, the left position is selected.
      }
      
      
      //speed limit - smoothing
      if(xSpeed > 0 && xSpeed<0.75)xSpeed = 0;
      //defines the speed in X to be equal to zero if smaller to 0.75 in module.
      if(xSpeed<0 && xSpeed> -0.75)xSpeed =0;
      //defines the speed in X to be equal to zero if smaller to 0.75 in module.
      if(xSpeed > 1)xSpeed= 1;
      //defines the speed in X to be equal to one if bigger to one in module.
      if(xSpeed< -1)xSpeed = -1;
      //defines the speed in X to be equal to one if bigger to one in module.
      
      
      
      
      
      //jump    
      
      //gravity
        ySpeed += 0.5;
        //constantly the y Speed increases, simulating gravity.
        
          //vertical collision
          hitBox.y += ySpeed;
          for(Platform p: panel.handler.platforms){
           //Checks each platform for a colision with this character;
              if(hitBox.intersects(p.hitBox)){
                hitBox.y -= ySpeed;
                
                while(!p.hitBox.intersects(hitBox)){
                    hitBox.y += Math.signum(ySpeed);
                }
                hitBox.y -= Math.signum(ySpeed);
                ySpeed = 0;
                y = hitBox.y;
            }
            //when there is intersection between the character hitbox and a platform hitbox on axis Y, the speed on Y will be decreased until it reaches zero.
            // the hitbox will be repositioned to start from the platform hitbox axis Y; The gravity will keep creating the colisions and the character will be constantly positioned on the intersection point.
        }
        
        //horizontal collision
        hitBox.x += xSpeed;
        
        for(Platform p: panel.handler.platforms){
            //Checks each platform for a colision with this character;
            if(hitBox.intersects(p.hitBox)){
                hitBox.x -= xSpeed;
               while(!p.hitBox.intersects(hitBox)){
                   hitBox.x += Math.signum(xSpeed);
               }
                hitBox.x -= Math.signum(xSpeed);
                xSpeed = xSpeed *(-1);
                x = hitBox.x;
            }
        }
        //when there is intersection between the character hitbox and a platform hitbox on axis X, the speed on X will be inverted
        
        y += ySpeed;
        hitBox.y = y;
        
        
        
        
      //Cause Damage to Player=====================================================================
      
        if(hitBox.intersects(player.hitBox)&& player.cState!= cState.Staggered && this.colour != colour.white){
           //Set Player State to Staggered, modifying the player object accordingly
            player.cState = cState.Staggered;
            player.spriteIndex= 1;
            player.health = player.health-1;
            //Stop Player input
            player.isInputEnable = false;
            //Create a new thread to handle the event
            cdThread = new myThread(player);
            //How much force we apply to the player on the X vector
            player.maxWalkingSpeed =25;
            //How much force we apply to the player on the Y vector
            player.ySpeed = -5;
            //Which direction to apply the push force
            if(player.x >= x){
                player.xSpeed = player.maxWalkingSpeed;
            }else {
                player.xSpeed = -player.maxWalkingSpeed;;
            }
            //As we are Disabling the player input for both cases ( Press and Release )
            //set all key input variables to false;
            player.keyFire = false;
            player.keyJump = false;
            player.keyLeft = false;
            player.keyRight = false;        
        }
        
        
        //============================================================================================
        
        //Updates Enemy X based on its Speed and ScreenScrolling
      
         
         if(camPrevX != panel.cameraX){
            
             x += (camPrevX-panel.cameraX);
             x += xSpeed;
             hitBox.x = x;
             camPrevX = panel.cameraX;
         }else{
             x+= xSpeed;
             hitBox.x = x;
         }
         
     
       
       
        // Character State Machine
        // this will check which state the character is , select the appropriated sprite sheet 
        switch(cState){
            case Walking:{
            
                //spriteSheetIndex = 0;
                sheetLenght = 6;
                break;
            }
            
            case Jumping:{
            
                //spriteSheetIndex=0;
                sheetLenght = 2;
                break;
            }
                      
        }
        
        
       
       Animation();
       
       //Check Collision vs Projectiles
       for( int i = 0 ; i < panel.handler.projectiles.size(); i++  ){
           
           if(panel.handler.projectiles.get(i).hitBox.intersects(this.hitBox) && colour!= colour.white){
               
               //Event ( dano)
               
               
               panel.handler.projectiles.remove(i);
               colour = colour.white;
               spriteSheetIndex = 1;
              
    }           
              }
           
       //for each zenith particle
            for(int i = 0 ; i < panel.handler.zenith.size(); i++  ){
           
                //if the hitBox intersects with the citizen hitbox AND the citizen colour is white
           if(panel.handler.zenith.get(i).hitBox.intersects(this.hitBox) && colour == colour.white){
               
               //Infection Event               
               //copy the zenith particle colour to the citizen
               colour = panel.handler.zenith.get(i).ct;
               //remove the zenith particle from the array
               panel.handler.zenith.remove(i);
               //import the new citizen image
               switch(colour){
                   case blue:{
                       spriteSheetIndex=0;
                       break;
                   }
                   case white:{
                       spriteSheetIndex=1;
                       break;
                   }
                   
                   case red:{
                       spriteSheetIndex=2;
                       break;
                   }
                   case yellow:{
                       spriteSheetIndex=3;
                       break;
                   }
                   case black:{
                       spriteSheetIndex=4;
                       break;
                   }
                   case pink:{
                       spriteSheetIndex=5;
                       break;
                   }
               }
               
              }
       
           
       }
       
    }
        
    
    
    }
    
    public void Roam(){
       
       xSpeed = xSpeed * -1;
    }
    
    public void SetSpeedX(double speed){
        movementSpeed =  speed;
        if(movementSpeed>0)direction = direction.Right;
           else if(movementSpeed<0)direction = direction.Left;
    }

    @Override
    public void Draw(Graphics2D gtd) {
     /// This rectangle represents the Object HitBox
      // gtd.setColor(Color.red);
      // gtd.fillRect(x, y, width, height); // Hitbox size
       //gtd.fillRect(ledgeBox.x, ledgeBox.y, ledgeBox.width, ledgeBox.height); // Other hitbox Approach
       //these lines flips the image horizontally and adjust their x and y coordinates to match the object HitBox
        if(direction == direction.Right)
            gtd.drawImage(characterSprite, x-30, y-30,100,100,panel);
                  //                                    /\ /\ 
//                                            (Size parameters, change both
        if(direction == direction.Left)
            gtd.drawImage(characterSprite, x+65, y-30,-100,100,panel);
    }
            
    //Load all character sprite sheets into memory and saves them into an array.
     public void ImportImage(){
         
         InputStream is = null;
         try{
         for(int i = 0; i<=5; i++){
             switch(i){
             case 0:
              is = getClass().getResourceAsStream("/Images/CitizenNew/CitizenBlue.png");
              sprites[i] = ImageIO.read(is);
             break;
             case 1:
             is = getClass().getResourceAsStream("/Images/CitizenNew/CitizenWhite.png");
             sprites[i] = ImageIO.read(is);
             break;
             case 2:
             is = getClass().getResourceAsStream("/Images/CitizenNew/CitizenRed.png");
             sprites[i] = ImageIO.read(is);
             break;
             case 3:
             is = getClass().getResourceAsStream("/Images/CitizenNew/CitizenGreen.png");
             sprites[i] = ImageIO.read(is);
             break;
             case 4:
             is = getClass().getResourceAsStream("/Images/CitizenNew/CitizenBlack.png");
             sprites[i] = ImageIO.read(is);
             break;
             case 5:
             is = getClass().getResourceAsStream("/Images/CitizenNew/CitizenPink.png");
             sprites[i] = ImageIO.read(is);
             break;    
         }
            
         }
         }catch(Exception e){
                  e.printStackTrace();
                 }
       /*
        try{
         sprites[i] = ImageIO.read(is);
                  
        }catch(Exception e){
            e.printStackTrace();
        }*/
    }
     
    // this method get subImages from a sprite sheet based on the x and y input coordinates
     public void UpdateImage(BufferedImage image,int x, int y){
        characterSprite = image.getSubimage(x, y, 64, 64);
        
    }
     
     public void Animation(){
        //Animation play rate, increments this variable every tick, the IF statement is where the actual rate is set
        //in short words it makes the image transition faster.
        animPlayRate++;
        if(animPlayRate == 8){
            
           //Every x Ticks get the new sub image from the sprite sheet
            if(spriteIndex<=sheetLenght){
                UpdateImage(sprites[spriteSheetIndex],(spriteIndex* 64)-64,0);
                spriteIndex++;
                 
                
            }else{
                spriteIndex = 1;
            }
            animPlayRate = 0;
           }
     }
}
