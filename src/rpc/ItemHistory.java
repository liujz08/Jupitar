package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Entity.Item;
import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			Set<Item> items = conn.getFavoriteItems(userId);
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.append("favorite", true);
				array.put(obj);
			}
			
			RpcHelper.writeJsonArray(response, array);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection dbconnection = DBConnectionFactory.getConnection();
		try {
			JSONObject jsobj = RpcHelper.readJSONObject(request);
			String userId = jsobj.getString("user_id");
			JSONArray jsarr = jsobj.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for(int i=0; i<jsarr.length(); i++) {
				itemIds.add(jsarr.getString(i)); 
			}
			dbconnection.setFavoriteItems(userId, itemIds);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(dbconnection != null) {
				dbconnection.close();
			}
		}		
		
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection dbconnection = DBConnectionFactory.getConnection();
		try {
			JSONObject jsobj = RpcHelper.readJSONObject(request);
			String userId = jsobj.getString("user_id");
			JSONArray jsarr = jsobj.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for(int i=0; i<jsarr.length(); i++) {
				itemIds.add(jsarr.getString(i)); 
			}
			dbconnection.unsetFavoriteItems(userId, itemIds);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(dbconnection != null) {
				dbconnection.close();
			}
		}	
	}

}
