package net.minecraft.src;

public class PlaceData{
	
	public static int rarity;
	public String fileName;
	public String foundinBiome;
	
	public PlaceData(String file, int rare, String biome)
	{
		rarity = rare;
		fileName = file;
		foundinBiome = biome;
	}
	
}