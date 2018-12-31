package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import db.mysql.MySQLConnection;
import Entity.Item;


public class GeoRecommendation {
	
	public List<Item> recommendItems(String userId, double lat, double lon){
		List<Item> recommendationItems = new ArrayList<>();
		DBConnection conn = DBConnectionFactory.getConnection();
		Set<String> favoritedItems = conn.getFavoriteItemIds(userId);
		
		Map<String, Integer> allCategories = new HashMap<>();
		for (String id: favoritedItems) {
			Set<String> categories = conn.getCategories(id);
			for(String category: categories) {
				allCategories.put(category, allCategories.getOrDefault(category, 0)+1);
			}
		}
		
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		
		Set<String> visitedItemIds = new HashSet<>();
		for(Entry<String, Integer> category: categoryList){
			List<Item> items = conn.searchItems(lat, lon, category.getKey());
			for(Item item: items) {
				if(!favoritedItems.contains(item.getItemId()) && !visitedItemIds.contains(item.getItemId())){
					recommendationItems.add(item);
					visitedItemIds.add(item.getItemId());
				}
			}
			
		}
		conn.close();
		
		return recommendationItems;		
	}
	
	
}
