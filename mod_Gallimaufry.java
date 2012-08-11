package net.minecraft.src;

// File IO form http://www.javacoffeebreak.com/faq/faq0004.html

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;

public class mod_Gallimaufry extends BaseMod
{

	public Map <String,List <PlaceData>> gallimaufry;
	
	public String getVersion()
	{
		return "1.3.1";
	}
	
	public void load()
	{
		    try{
			
		    // Input setup to read in the list of sites.
			
			InputStream listOfPlaces = new FileInputStream (Minecraft.getMinecraftDir() + "/things/structList.txt");
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
							
				List <PlaceData> tempList = gallimaufry.get(storeme.foundinBiome);
				int x = 0;
				while(x < tempList.size())
				{
				
					if(storeme.rarity > tempList.get(x).rarity) {break;}
					x++;
				}
				gallimaufry.get(storeme.foundinBiome).add(x, storeme);
				
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
			e.printStackTrace();
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
				//System.out.println(world.getBiomeGenForCoords(i, j));
				buildSite (sitesForBiome.get(x).fileName, i , j, world);
				break;
			}
			
			x++;
				
		}
	}
	
	public void buildSite (String fileForSite, int i, int j, World world)
	{
		System.out.println("Building " + fileForSite);
		try{
			File temp = new File (Minecraft.getMinecraftDir() + "/things/" + fileForSite);
		InputStream placeFile = new FileInputStream (Minecraft.getMinecraftDir() + "/things/" + fileForSite);
		BufferedReader placeLines = new BufferedReader (new InputStreamReader(placeFile));
		String type;
		String currLine;
		StringTokenizer breaker;
		ItemStack []  contents = new ItemStack [36];
		// clears the comment line
		placeLines.readLine();
		int zplane = i;
		int yplane = 62;
		int xplane = j;
		while((currLine = placeLines.readLine()) != null )
		{
			breaker = new StringTokenizer(currLine);
			while (breaker.hasMoreTokens())
			{
				String nextToken = breaker.nextToken();
				switch(nextToken.charAt(0))
				{
				 case 'H':
					 // Sets the height. Intended to be used at the beginning of a site
					 StringTokenizer heighter =  new StringTokenizer(nextToken);
					 type = heighter.nextToken(":");
					 String height = heighter.nextToken();
					 yplane = Integer.parseInt(height);
					 zplane = i - 1;
					 break;
				 case 'B':
					 // Sets the block for the current space
					StringTokenizer blocker =  new StringTokenizer(nextToken);
					type = blocker.nextToken("(");
					String blockID = blocker.nextToken(",");
					blockID = blockID.substring(1);
					String metaData = blocker.nextToken(")");
					metaData = metaData.substring(1);
					world.setBlockAndMetadata(xplane, yplane, zplane, Integer.parseInt(blockID), Integer.parseInt(metaData));
					break;
				 case 'T':
					 // Intended to be the case for tile entities.
					 StringTokenizer tiler = new StringTokenizer(nextToken);
					 type = tiler.nextToken("(");
					 String tileID = tiler.nextToken(",");
					 blockID = tileID.substring(1);
					 String tmetaData = tiler.nextToken(")");
					 TileEntity tile;
					 metaData = tmetaData.substring(1);
					 
					//Piston 
					if (Integer.parseInt(blockID) == 29)
					{   
						world.setBlockAndMetadata(xplane, yplane, zplane, Integer.parseInt(blockID),Integer.parseInt(metaData));
					}
					//Chest
					if (Integer.parseInt(blockID) == 54)
					{
						TileEntityChest chest = new TileEntityChest();
						world.setBlock(xplane, yplane, zplane, Integer.parseInt(blockID));
						world.setBlockTileEntity(xplane, yplane, zplane, chest);
						chest.blockMetadata = Integer.parseInt(metaData);
						
						chest.setInventorySlotContents(2, contents[0]);
						world.setBlockMetadata(xplane, yplane, zplane,Integer.parseInt(metaData));
						//world.setBlockAndMetadata(xplane, yplane, zplane, Integer.parseInt(blockID),Integer.parseInt(metaData));
					}
					  break;
				 case 'C':
					 //Sets container contents
					 StringTokenizer container = new StringTokenizer(nextToken);
					 type = container.nextToken("(");
					 String contentsToStore = container.nextToken();
					 StringTokenizer contenter = new StringTokenizer(contentsToStore);
					 while(contenter.hasMoreTokens())
					 {
						 String itemStack = contenter.nextToken(",").substring(1);
						 if (itemStack.contentEquals(")"))
						 {
							 break;
						 }
						 Item myItem =new Item(100);
						 int test = myItem.shiftedIndex;
						 String temp2 = contenter.nextToken("}").substring(1);
						 ItemStack myStack = new ItemStack(myItem, Integer.parseInt(temp2));
						 contents[0] = myStack;
					 }
					 zplane = i - 1;
					 break;
				 case 'E':
					 // Intended to be the case for normal mobs and other entities like carts
					 StringTokenizer entiter = new StringTokenizer(nextToken);
					 type = entiter.nextToken("(");
					 spawnEntity(entiter.nextToken(",").substring(1),world,xplane,yplane,zplane);
					 break;
				 case 'U':
					 yplane++;
					zplane = i - 1;
					continue;
				 case 'S':
					 break;
				}
				xplane++;
			}
			xplane = j;
			zplane++;
		}
		return;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/****
	// This spawns the entity passed into the world at the specified XYZ.
	*****/
    public void spawnEntity(String entityToSpawn,World world, int xplane, int yplane, int zplane)
    {    
    	//Spawn a creeper
    	if(entityToSpawn.contentEquals("Cr"))
    	{	
    		EntityCreeper creeper = new EntityCreeper(world);
    		creeper.entityAge = -24000;
    		creeper.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(creeper);
    		return;
    	}
    	//Spawn an Enderman
    	if(entityToSpawn.contentEquals("En"))
    	{
    		EntityEnderman enderman = new EntityEnderman(world);
    		enderman.entityAge = -24000;
    		enderman.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(enderman);
    		return;
    	}
    	//Spawn a Skeleton
    	if(entityToSpawn.contentEquals("Sk"))
    	{
    		EntitySkeleton skeleton = new EntitySkeleton(world);
    		skeleton.entityAge = -24000;
    		skeleton.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(skeleton);
    		return;
    	}
    	//Spawn a Zombie
    	if(entityToSpawn.contentEquals("Zo"))
    	{
    		EntityZombie zombie = new EntityZombie(world);
    		zombie.entityAge = -24000;
    		zombie.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(zombie);
    		return;
    	}
    	//Spawn a Slime
    	if(entityToSpawn.contentEquals("Sl"))
    	{
    		EntitySlime slime = new EntitySlime(world);
    		slime.entityAge = -24000;
    		slime.setSlimeSize(3);
    		slime.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(slime);
    		return;
    	}
    	//Spawn a Spider
    	if(entityToSpawn.contentEquals("Sp"))
    	{
    		EntitySpider spider = new EntitySpider(world);
    		spider.entityAge = -24000;
    		spider.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(spider);
    		return;
    	}
    	//Spawn a Cave Spider
    	if(entityToSpawn.contentEquals("Cs"))
    	{
    		EntityCaveSpider cavespider = new EntityCaveSpider(world);
    		cavespider.entityAge = -24000;
    		cavespider.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(cavespider);
    		return;
    	}
    	//Spawn a Ghast
    	if(entityToSpawn.contentEquals("Gh"))
    	{
    		EntityGhast ghast = new EntityGhast(world);
    		ghast.entityAge = -24000;
    		ghast.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(ghast);
    		return;
    	}
    	//Spawn a Blaze
    	if(entityToSpawn.contentEquals("Bl"))
    	{
    		EntityBlaze blaze = new EntityBlaze(world);
    		blaze.entityAge = -24000;
    		blaze.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(blaze);
    		return;
    	}
    	//Spawn a Magma Cube
    	if(entityToSpawn.contentEquals("Ma"))
    	{
    		EntityMagmaCube magmacube = new EntityMagmaCube(world);
    		magmacube.entityAge = -24000;
    		magmacube.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(magmacube);
    		return;
    	}
    	//Spawn a Dragon
    	if(entityToSpawn.contentEquals("Dr"))
    	{
    		EntityDragon dragon = new EntityDragon(world);
    		dragon.entityAge = -24000;
    		dragon.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(dragon);
    		return;
    	}
    	//Spawn a sheep
    	if(entityToSpawn.contentEquals("Sh"))
    	{
    		EntitySheep sheep = new EntitySheep(world);
    		sheep.entityAge = -2;
    		sheep.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(sheep);
    		return;
    	}
    	//Spawn a Cow
    	if(entityToSpawn.contentEquals("Co"))
    	{
    		EntityCow cow = new EntityCow(world);
    		cow.entityAge = -2;
    		cow.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(cow);
    		return;
    	}
    	//Spawn a Pig
    	if(entityToSpawn.contentEquals("Pi"))
    	{
    		EntityPig pig = new EntityPig(world);
    		pig.entityAge = -2;
    		pig.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(pig);
    		return;
    	}
    	//Spawn a Chicken
    	if(entityToSpawn.contentEquals("Ch"))
    	{
    		EntityChicken chicken = new EntityChicken(world);
    		chicken.entityAge = -2;
    		chicken.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(chicken);
    		return;
    	}
    	//Spawn a Ocelot
    	if(entityToSpawn.contentEquals("Ch"))
    	{
    		EntityOcelot ocelot = new EntityOcelot(world);
    		ocelot.entityAge = -2;
    		ocelot.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(ocelot);
    		return;
    	}
       	//Spawn a Pig Zombie
    	if(entityToSpawn.contentEquals("Pz"))
    	{
    		EntityPigZombie pigzombie = new EntityPigZombie(world);
    		pigzombie.entityAge = -24000;
    		pigzombie.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(pigzombie);
    		return;
    	}
       	//Spawn a boat
    	if(entityToSpawn.contentEquals("Bo"))
    	{
    		EntityBoat boat = new EntityBoat(world);
    		//boat.entityAge = -2;
    		boat.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(boat);
    		return;
    	}
       	//Spawn a Iron Golem
    	if(entityToSpawn.contentEquals("Ir"))
    	{
    		EntityIronGolem irongolem = new EntityIronGolem(world);
    		irongolem.entityAge = -24000;
    		irongolem.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(irongolem);
    		return;
    	}
       	//Spawn a Giant Zombie
    	if(entityToSpawn.contentEquals("Gi"))
    	{
    		EntityGiantZombie giant = new EntityGiantZombie(world);
    		giant.entityAge = -24000;
    		giant.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(giant);
    		return;
    	}
       	//Spawn a Mine Cart
    	if(entityToSpawn.contentEquals("Mi"))
    	{
    		EntityMinecart cart = new EntityMinecart(world);
    		//cart.entityAge = -24000;
    		cart.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(cart);
    		return;
    	}
       	//Spawn a Mooshroom
    	if(entityToSpawn.contentEquals("Gi"))
    	{
    		EntityMooshroom moosh = new EntityMooshroom(world);
    		moosh.entityAge = -2;
    		moosh.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(moosh);
    		return;
    	}
       	//Spawn a Sliver Fish
    	if(entityToSpawn.contentEquals("Si"))
    	{
    		EntitySilverfish silverfish = new EntitySilverfish(world);
    		silverfish.entityAge = -24000;
    		silverfish.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(silverfish);
    		return;
    	}
       	//Spawn a Snowman
    	if(entityToSpawn.contentEquals("Sn"))
    	{
    		EntitySnowman frosty = new EntitySnowman(world);
    		frosty.entityAge = -24000;
    		frosty.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(frosty);
    		return;
    	}
       	//Spawn a Squid
    	if(entityToSpawn.contentEquals("Sq"))
    	{
    		EntitySquid squid = new EntitySquid(world);
    		squid.entityAge = -2;
    		squid.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(squid);
    		return;
    	}
       	//Spawn a Villager
    	if(entityToSpawn.contentEquals("Vi"))
    	{
    		EntityVillager villager = new EntityVillager(world);
    		villager.entityAge = -2;
    		villager.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(villager);
    		return;
    	}
       	//Spawn a Wolf
    	if(entityToSpawn.contentEquals("Wo"))
    	{
    		EntityWolf wolf = new EntityWolf(world);
    		wolf.entityAge = -2;
    		wolf.setLocationAndAngles(xplane, yplane, zplane, 0, 0.0F);
    		world.spawnEntityInWorld(wolf);
    		return;
    	}
    }
	


}