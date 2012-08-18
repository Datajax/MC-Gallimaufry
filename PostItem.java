package net.minecraft.src;

//File IO form http://www.javacoffeebreak.com/faq/faq0004.html
//Copyright (c) 2012 Robert Fesler
//Published under MIT license see COPYING.txt for details

public class PostItem
{
	public int blockID;
	public int metaData;
	public int xplane;
	public int yplane;
	public int zplane;
	
	public PostItem(int ID, int data, int x, int y, int z)
	{
		blockID = ID;
		metaData = data;
		xplane = x;
		yplane = y;
		zplane = z;
	}
	
}