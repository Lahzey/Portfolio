package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.CoalPlant;
import bankcity.gamelogic.buildings.types.NuclearPlant;
import bankcity.gamelogic.buildings.types.SolarPlant;
import bankcity.gamelogic.buildings.types.WindTurbine;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;
import bankcity.graphics.systems.RenderingSystem.BuildingOverlay;
import bankcity.graphics.systems.RenderingSystem.Overlay;

public class Electricity implements BuildingCategory{

	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new CoalPlant(game));
		buildings.add(new SolarPlant(game));
		buildings.add(new WindTurbine(game));
		buildings.add(new NuclearPlant(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "electricity.png";
	}

	@Override
	public String getName() {
		return "Electricity";
	}

	@Override
	public String getDescription() {
		return "Provides the city with electricity needed for most buildings.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.SERVICES;
	}
	
	public static abstract class SimpleElectricityBuilding extends Building{

		public SimpleElectricityBuilding(Game game, float price, float upkeep, int workerCount, float energyProduction, float waterConsumption) {
			super(game, price, 0, upkeep, -energyProduction, waterConsumption);
			functionalities.add(new JobFunctionality(EducationLevel.LOW, workerCount));
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.SERVICES;
		}
		
		@Override
		public Overlay getOverlay(){
			return new BuildingOverlay(){

				@Override
				public boolean isTarget(Building building) {
					return true;
				}
				
				@Override
				public void getColor(Building building, Color colorInstance){
					if(building instanceof SimpleElectricityBuilding){
						super.getColor(building, colorInstance);
					}else{
						boolean hasElectricity = !game.resourceSystem.unsatisfied.get(Resource.ELECTRICITY).contains(building.getEntity());
						colorInstance.r = hasElectricity ? 0 : 1;
						colorInstance.g = hasElectricity ? 1 : 0;
						colorInstance.b = 0;
					}
				}
				
			};
		}
		
	}

}
