package net.minecraft.src;

// File IO form http://www.javacoffeebreak.com/faq/faq0004.html

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map;

public class mod_Gallimaufry extends BaseMod
{

	public Map <String,List <PlaceData>> gallimaufry;
	
	
	public String getVersion()
	{
		return "1.2.5";
	}
	
	public void load()
	{
				try{
			
		    // Input setup to read in the list of sites.
			InputStream listOfPlaces = new FileInputStream ("things\\structList.txt");
			BufferedReader bufferedPlaces = new BufferedReader (new InputStreamReader(listOfPlaces));
			
			// To become a map of Biomes to places
			List <PlaceData> sites = new ArrayList <PlaceData>();
			gallimaufry = new  HashMap <String,List <PlaceData>>();
			gallimaufry.put("All", sites);
			bufferedPlaces.readLine(); // Gets rid of the commented line at the top of the file
			String place;
			StringTokenizer breaker;
			// Read in the list of places and build the master list for generation
			// needs to be changed for insertion sorting
			while((place = bufferedPlaces.readLine()) != null)
			{
			breaker = new StringTokenizer(place);
		    PlaceData storeme = new PlaceData(breaker.nextToken(),Integer.decode(breaker.nextToken()),breaker.nextToken());
			if(gallimaufry.containsKey(storeme.foundinBiome))
			{
				//sort before this step in most rare to least rare
				gallimaufry.get(storeme.foundinBiome).add(storeme);
			}else{
				List <PlaceData> tempList =  new ArrayList <PlaceData>();
				gallimaufry.put(storeme.foundinBiome, tempList);
				gallimaufry.get(storeme.foundinBiome).add(storeme);
			}
		    //System.out.print(sites.get(0).fileName);
			}
			listOfPlaces.close();
			
		} catch (IOException e)
		{
			System.err.println("Unable to read from file");
			System.exit(-1);
		}
	}
	
	public void generateSurface(World world, Random random, int i, int j) 
   {
		System.out.println(world.getBiomeGenForCoords(i, j).biomeName);
		String usingBiome = "All";
		if(gallimaufry.containsKey(world.getBiomeGenForCoords(i, j).biomeName))
		{
			usingBiome = world.getBiomeGenForCoords(i, j).biomeName;
		}
		List <PlaceData> sitesForBiome = new ArrayList <PlaceData>();
		sitesForBiome = gallimaufry.get(usingBiome);
			
		int x = 0;
		while(x < sitesForBiome.size())
		{
			//Looking for a site to use based on rarest first
			//build site once it is found

			if(random.nextInt() % sitesForBiome.get(x).rarity == 0)
			{
				buildSite (sitesForBiome.get(x).fileName, i , j, world);
				break;
			}
			
			x++;
				
		}
	}
	
	public void buildSite (String fileForSite, int i, int j, World world)
	{
		try{
		InputStream placeFile = new FileInputStream ("things\\" + fileForSite);
		BufferedReader placeLines = new BufferedReader (new InputStreamReader(placeFile));
		String currLine;
		StringTokenizer breaker;
		// clears the comment line
		placeLines.readLine();
		int zplane = i;
		int yplane = 80;
		int xplane = j;
		while((currLine = placeLines.readLine()) != null )
		{
			breaker = new StringTokenizer(currLine);
			while (breaker.hasMoreTokens())
			{
				String nextToken = breaker.nextToken();
				if(nextToken.startsWith("B"))
				{
					// x y z blockid
					StringTokenizer blocker =  new StringTokenizer(nextToken);
					String type = blocker.nextToken("(");
					String blockID = blocker.nextToken(",");
					blockID = blockID.substring(1);
					String metaData = blocker.nextToken(")");
					metaData = metaData.substring(1);
					world.setBlockAndMetadata(xplane, yplane, zplane, Integer.parseInt(blockID), Integer.parseInt(metaData));
				}
				if(nextToken.startsWith("U"))
				{
					yplane++;
					zplane = i - 1;
					continue;
				}
				xplane++;
			}
			xplane = j;
			zplane++;
		}
		return;
		} catch (IOException e)
		{
			System.err.println("Unable to read from file");
			System.exit(-1);
		}
	}
   
	
}