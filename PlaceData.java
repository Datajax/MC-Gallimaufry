package net.minecraft.src;

//File IO form http://www.javacoffeebreak.com/faq/faq0004.html
//Copyright (c) 2012 Robert Fesler
//Published under MIT license see COPYING.txt for details

public class PlaceData{
	
	public int rarity;
	public String fileName;
	public String foundinBiome;
	
	public PlaceData(String file, int rare, String biome)
	{
		rarity = rare;
		fileName = file;
		foundinBiome = biome;
	}
	
}