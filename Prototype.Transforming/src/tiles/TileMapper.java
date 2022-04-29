/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zMapEditor;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import platformergame.Handler;
import platformergame.Platform;



/**
 *
 * @author diego
 */
public class TileMapper {
  public int tilesetWidth;
  public int tilesetHeight;
  public int tileWidth;
  public int tileHeight;
  public Long lTemp;
  public String[][]values ;
  public String[] temp;
  public BufferedImage[] allTiles;
  public int rowCounter = 0;
  public BufferedImage tileSet;
  
  public int[][] tilesetData;
  public int[][] backgroundData;
  public int[][] decorationData;
  public int[][] objectsData;
  public int[][] solidData;
  Platform platform;
  Handler handler;
  
  
   
    
    
    public TileMapper(String mapName, Handler h){
    this.handler = h;
    JSONParser parser = new JSONParser();
    try{
        //Read the json file and load it into memory
        Object obj = parser.parse(new FileReader("src\\zMapEditor\\Maps\\"+mapName+".json"));
        // convert Object into a json Object
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray layers = (JSONArray)jsonObject.get("layers");
        JSONArray tileInfo = (JSONArray)jsonObject.get("tilesets");
        Long heights = (Long)jsonObject.get("height");
       
        for(int i = 0; i<layers.size(); i++){
           //Get data from json array at index "i"
            jsonObject =(JSONObject) layers.get(i);
            
            //Convert json data to string
           String data = jsonObject.get("data").toString();
           //format string variable "data" removing empty spaces, special characters
           //this variable holds a the values of a json array, they were converted to a String, this String will be split into individual values and then added to an Java array
           data = data.replaceAll("[\\[\\](){}]","");
           //Gets the layer name that will be used later to build the map
           String layerName = (String)jsonObject.get("name");
            
            //This is a temporary variable that will hold the values to be added to the 2d array
             String[]tempo;
            tempo = data.split(",");
           
             lTemp = (Long)jsonObject.get("width");
             tilesetWidth = lTemp.intValue();
             
             lTemp = (Long)jsonObject.get("height");
             tilesetHeight = lTemp.intValue();
             
           
           
        
            
            
            
            
            
            
            switch(layerName){
                case "TileMap":{
                    tilesetData = new int[tilesetHeight][tilesetWidth];
                     int index = 0;
                     for(int j = 0; j<tilesetHeight; j++){
                         for(int k = 0; k<tilesetWidth; k++){
                             this.tilesetData[j][k] = Integer.parseInt(tempo[index]);
                              index++;
                              
                         }
             
             
            }
                     jsonObject = (JSONObject)tileInfo.get(0);
             lTemp = (Long)jsonObject.get("tilewidth");
             tileWidth = lTemp.intValue();
             lTemp = (Long)jsonObject.get("tileheight");
             tileHeight = lTemp.intValue();
                      
                    
                     break;
                     
                }
                
                case "BackGround":{
                    backgroundData = new int[tilesetHeight][tilesetWidth];
                    int index = 0;
                     for(int j = 0; j<tilesetHeight; j++){
                         for(int k = 0; k<tilesetWidth; k++){
                             backgroundData[j][k] =Integer.parseInt(tempo[index]);
                              index++;
                         }
                     }break;
                }
                
                case "Decoration":{
                    decorationData = new int[tilesetHeight][tilesetWidth];
                    int index = 0;
                     for(int j = 0; j<tilesetHeight; j++){
                         for(int k = 0; k<tilesetWidth; k++){
                             decorationData[j][k] = Integer.parseInt(tempo[index]);
                              index++;
                         }
            }break;
                }
                
                case "Objects":{
                    objectsData = new int[tilesetHeight][tilesetWidth];
                    int index = 0;
                     for(int j = 0; j<tilesetHeight; j++){
                         for(int k = 0; k<tilesetWidth; k++){
                             objectsData[j][k] = Integer.parseInt(tempo[index]);
                              index++;
                         }
              
            }break;
                }
                
                case "Solid":{
                    solidData = new int[tilesetHeight][tilesetWidth];
                    
                    int index = 0;
                     for(int j = 0; j<tilesetHeight; j++){
                         for(int k = 0; k<tilesetWidth; k++){
                             solidData[j][k] = Integer.parseInt(tempo[index]);
                              index++;
                         }
              
            }break;
                    
                }
            }
           
        }
        LoadTiles(mapName,tilesetWidth,tilesetHeight);
       
        
        
       
        
    }catch(Exception e){
        e.printStackTrace();
    }
        GenerateMap();
    }
 
    public void LoadTiles(String mapName,int width,int height){
        InputStream is = getClass().getResourceAsStream("/Images/Map1/"+mapName+"TS.png");
       
            try{
                tileSet = ImageIO.read(is);
                
            }catch(Exception e){e.printStackTrace();}
                
            
            allTiles = new BufferedImage[tilesetHeight*tilesetWidth];
           
              for(int i = 0; i<20; i++){
           for(int j = 0; j<30; j++){
             //  String temp = values[i][j];
               int p = tilesetData[i][j];
               
               allTiles[p]= tileSet.getSubimage(j*tileWidth, i*tileHeight, tileWidth, tileHeight);
               
           }
       }
          
         
    }
    
    public void GenerateMap(){
        int scaleImage = 1;
        
        for(int i = 0; i<tilesetHeight; i++){
            for(int j = 0; j<tilesetWidth; j++){
                if(backgroundData[i][j]!= 0){
                    platform = new Platform((j*tileWidth)*scaleImage, (i*tileHeight)*scaleImage, tileWidth*scaleImage, tileHeight*scaleImage,GetTile(backgroundData[i][j]),false);
                handler.addTile(platform);
                }
                
                 if(decorationData[i][j]!= 0){
                    platform = new Platform((j*tileWidth)*scaleImage, (i*tileHeight)*scaleImage, tileWidth*scaleImage, tileHeight*scaleImage,GetTile(decorationData[i][j]),false);
                handler.addTile(platform);
                }
                
                if(solidData[i][j]!= 0){
                platform = new Platform((j*tileWidth)*scaleImage, (i*tileHeight)*scaleImage, tileWidth*scaleImage, tileHeight*scaleImage,GetTile(solidData[i][j]),true);
                handler.addPlatform(platform);
                }
                
                if(objectsData[i][j]!= 0){
                platform = new Platform((j*tileWidth)*scaleImage, (i*tileHeight)*scaleImage, tileWidth*scaleImage, tileHeight*scaleImage,GetTile(objectsData[i][j]),false);
                handler.addTile(platform);
                }
                
            }
        }
    }
    
    public void Draw(Graphics2D g){
       
      
  }
    
   public BufferedImage GetTile(int index){
    
       return allTiles[index];
}
  
  
  
    
}