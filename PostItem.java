package net.minecraft.src;

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