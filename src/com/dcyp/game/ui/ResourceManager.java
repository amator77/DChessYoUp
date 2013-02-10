package com.dcyp.game.ui;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.cyp.application.Application;

public class ResourceManager {
	private static final ResourceManager manager = new ResourceManager();
	
	private Map<String, Image> images;
	
	private Map<String, Icon> icons;
	
	private ResourceManager(){
		this.images = new HashMap<String, Image>();
		this.icons = new HashMap<String, Icon>();
	}
	
	public static ResourceManager getManager(){
		return ResourceManager.manager;
	}
	
	public Image loadImage(String resource){
		Image img = images.get("resources/"+resource);
		
		if( img == null ){
			try {
				img = ImageIO.read(Application.getContext().getResourceAsInputStream("resources/"+resource));
				images.put(resource, img);
			} catch (IOException e) {
				Application.getContext().getLogger().error("ResourceManager","Error loading :"+resource, e);				
			}
		}
		
		return img;
	}
	
	public Icon loadImageAsIcon(String resource){
		
		Icon icon = icons.get(resource);
		
		if( icon == null ){
			
			Image img = loadImage(resource);
			
			if( img != null ){
				icon = new ImageIcon(img);
				icons.put(resource, icon);
				return icon;
			}
			else{
				return null;
			}
		}
		else{
			return icon;
		}		
	}
	
	public Icon loadImageAsIcon(String resource , int width , int heigh){
		
		Icon icon = icons.get(resource);
		
		if( icon == null ){
			
			Image img = loadImage(resource);
			
			if( img != null ){
				icon = new ImageIcon(img.getScaledInstance(width, heigh, Image.SCALE_SMOOTH));
				icons.put(resource, icon);
				return icon;
			}
			else{
				return null;
			}
		}
		else{
			return icon;
		}		
	}
	
	public Icon createAvatarIcon(byte[] imageData){		
		if( imageData != null ){
			return new ImageIcon(Toolkit.getDefaultToolkit().createImage(imageData).getScaledInstance(36, 36, Image.SCALE_SMOOTH));      
		}
		else{
			return loadImageAsIcon("general_avatar_unknown.png");      
		}
	}
}
