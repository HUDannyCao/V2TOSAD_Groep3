package define.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import define.model.Attribute_Compare;
import persistence.OracleBaseDao;

//
public class Attribute_CompareDaoImpl extends OracleBaseDao implements Attribute_CompareDao {

	private BusinessRuleDao bdao = new BusinessRuleOracleDaoImpl();

	public Attribute_Compare save(Attribute_Compare compare) {
		try (Connection con = getConnection()) {
			Statement stmt = con.createStatement();


			int id = bdao.createUniqueID();
			int type = 2;
			String constraintNaam = "BRG_VBMG_" + compare.getTable().toUpperCase() + "_CNS_ACMP_"+compare.getId();
			String businessruleNaam = "BRG_VBMG_" + compare.getTable().toUpperCase() + "_CNS_ACMP_"+compare.getId();

			String query = "INSERT INTO constraint (id, naam, table_name ,attribute_name, operator, value,type)VALUES('" + compare.getId() + "', '"+
					constraintNaam + "', '" + compare.getTable() +  "', '" + compare.getAttribute() + "', '" + compare.getOperator() + "', '" + compare.getValue()+ "', '" + "check"  + "')";
			String query1 = "INSERT INTO businessrule (id, naam,businessruletypeid,constraintid)VALUES('" +id + "', '"+
					businessruleNaam + "', '" + type + "', '" + compare.getId()+ "')";

			stmt.executeUpdate(query);
			stmt.executeUpdate(query1);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return compare;
	}

	public ArrayList<Attribute_Compare> selectConstraintCompare(String query) {
		ArrayList<Attribute_Compare> results = new ArrayList<Attribute_Compare>();
		try (Connection con = super.getConnection()){
			Statement stmt = con.createStatement();
			ResultSet dbResultSet = stmt.executeQuery(query);

			while (dbResultSet.next()) {
				int constraintnummer = dbResultSet.getInt("id");
				String naam = dbResultSet.getString("naam");
				String table = dbResultSet.getString("table_name");
				String atribuut = dbResultSet.getString("ref_attribute");
				String operator = dbResultSet.getString("operator");
				String value = dbResultSet.getString("value");




				Attribute_Compare beperking = new Attribute_Compare(table,naam,constraintnummer,atribuut,operator,value);


				results.add(beperking);
			}
		} catch (SQLException sqle) { sqle.printStackTrace(); }

		return results;
	}

	public boolean updateCompare (Attribute_Compare compare) throws SQLException {
		Connection c = super.getConnection();
		PreparedStatement ps = c.prepareStatement("UPDATE constraint SET naam = ?,table_name = ?, ref_attribute = ?, operator = ?, value = ? WHERE id = ?");
		PreparedStatement ps1 = c.prepareStatement("UPDATE businessrule SET naam = ? WHERE constraintid = ?");
		ps.setString(1, "BRG_VBMG_" + compare.getTable().toUpperCase() + "_CNS_ACMP_"+compare.getId());
		ps.setString(2, compare.getTable());
		ps.setString(3, compare.getAttribute());
		ps.setString(4, compare.getOperator());
		ps.setString(5, compare.getValue());
		ps.setInt(6,compare.getId());
		ps1.setString(1,"BRG_VBMG_" + compare.getTable().toUpperCase() + "_CNS_ACMP_"+compare.getId());
		ps1.setInt(2,compare.getId());
		boolean result = ps.executeUpdate() > 0;
		boolean result1 = ps1.executeUpdate() > 0;
		ps.close();
		c.close();
		return result;


	}

	public ArrayList<Attribute_Compare> findAllCompare() {
		return selectConstraintCompare("SELECT * from constraint");
	}

}
