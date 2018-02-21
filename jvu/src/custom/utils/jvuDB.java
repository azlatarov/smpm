package custom.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// ResultSet count
//Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//rs = stmt.executeQuery(select_query);
//rs.last();
//rs.getRow();


public final class jvuDB {
	
	private jvuDB () {}
	
	/**
	 * Release connection if not null and if not done already.
	 * @author	Atanas P. Zlatarov
	 */
	public static void closeResource(Connection c) throws SQLException {
		if(c != null && !c.isClosed())
			c.close();
	}
	
	/**
	 * Release PreparedStatement if not null and if not done already.
	 * @author	Atanas P. Zlatarov
	 */
	public static void closeResource(PreparedStatement ps) throws SQLException {
		if(ps != null && !ps.isClosed())
			ps.close();
	}
	
	/**
	 * Release ResultSet if not null and if not done already.
	 * @author	Atanas P. Zlatarov
	 */
	public static void closeResource(ResultSet rs) throws SQLException {
		if(rs != null && !rs.isClosed())
			rs.close();
	}
	
	/**
	 * Executes 'query' and retrieves values from column 'fieldOfInterest'. The existing values are put in first array, and nonexisting values in second.
	 * @param 	conn					- 
	 * @param 	currentRange			- the range to use in IN-clause of the query
	 * @param 	query					- the query to execute to get results from DB
	 * @param 	columnOfInterest		- the column of interest, for which to create the arrays of existing/nonexisting values
	 * @param 	alsoRemoveSingleQuotes	- if single quotes should also be stripped when convertRangeBackToArray is called
	 * @return
	 * @author	Atanas P. Zlatarov
	 */
	public static String[][] getExistingAndNonexistingFromDB (Connection conn, String currentRange, String query, String columnOfInterest, boolean alsoRemoveSingleQuotes) {
		String[][] result = new String[2][];
		Statement stmt = null;
		ResultSet rs = null;
		
		String[] currentRangeArray = convertRangeBackToArray(currentRange, alsoRemoveSingleQuotes);
		ArrayList<String> existingInDB = new ArrayList<String>(Arrays.asList(currentRangeArray));
		ArrayList<String> nonexistingInDB = new ArrayList<String>(Arrays.asList(currentRangeArray));
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
				nonexistingInDB.remove(rs.getString(columnOfInterest));
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		existingInDB.removeAll(nonexistingInDB);
		
		String[] existingBpscodes = new String[existingInDB.size()];
		String[] nonexistingBpscodes = new String[nonexistingInDB.size()];
		existingInDB.toArray(existingBpscodes);
		nonexistingInDB.toArray(nonexistingBpscodes);
		existingBpscodes = buildRangesFromArrayOfIds(existingBpscodes, true);
		nonexistingBpscodes = buildRangesFromArrayOfIds(nonexistingBpscodes, true);
		
		result[0] = existingBpscodes;
		result[1] = nonexistingBpscodes;
		
		return result;
	}
	
	/**
	 * Take an array of Ids and build an array of Ranges
	 * @param 	array							 - data to split in ranges
	 * @param 	putSingleQuotesAroundRangeValues - false if column of type NUMBER (not to prevent DB indexing), and true otherwise
	 * @return
	 * @author	Atanas P. Zlatarov
	 */
	public static String[] buildRangesFromArrayOfIds(String[] array, boolean putSingleQuotesAroundRangeValues) {
		String[] arrayOfRanges = new String[1];
		StringBuffer range = null;
		int maxIdsAtOnce = 1000;	// The maximum number of IDs to have in a single range
		int currentPosition = 0;
		int endPosition = maxIdsAtOnce;
		int arrayOfRangesIndex = 0;
		boolean continueWritingToArrayOfRanges = true;
		
		if(array.length == 0) {
			arrayOfRanges[0] = "";
			return arrayOfRanges;
		}
		
		array = jvuDataStructures.clean(array, new Object[]{""});
		
		while(continueWritingToArrayOfRanges) {
			range = new StringBuffer();
			String comma = ",";
			int j = currentPosition;
			while (j < endPosition && j != array.length) {
				if(putSingleQuotesAroundRangeValues) 
					range.append(jvuString.surround(array[j],"'"));
				else 
					range.append(array[j]);
				
				range.append(comma);
				j++;
			}
			
			range = range.replace(range.length() - comma.length(), range.length(), "");
			arrayOfRanges[arrayOfRangesIndex] = jvuString.surround(range.toString(),"()");
			
			if(endPosition >= array.length)
				continueWritingToArrayOfRanges = false; // Stop writing to arrayOfRanges
			else {
				// Write to arrayOfRanges once more
				arrayOfRangesIndex++;
				currentPosition = endPosition;
				endPosition += maxIdsAtOnce;
				arrayOfRanges = jvuDataStructures.expand(arrayOfRanges, 1);
			}
		}
		
		return arrayOfRanges;
	}
	
	/**
	 * Converts a string containing a range of ids/bpscodes/etc. back to an array of string
	 * @param 	rangeStr
	 * @param	alsoRemoveSingleQuotes
	 * @return
	 * @author 	Atanas P. Zlatarov
	 */
	public static String[] convertRangeBackToArray (String rangeStr, boolean alsoRemoveSingleQuotes) {
		String[] result = null;
		
		rangeStr = jvuString.trim(rangeStr,"()");
		result = rangeStr.split(",");
		if(alsoRemoveSingleQuotes) 
			for (int i = 0; i < result.length; i++)
				result[i] = jvuString.trim(result[i],"'");
		
		return result;
	}
	
	/**
	 * Extract Clob bytes.
	 * @author 	Atanas P. Zlatarov
	 */
	public static byte[] getClobBytes (ResultSet rs, String columnName) throws SQLException, IOException {
		return jvuIO.reader2bytes(rs.getClob(columnName).getCharacterStream());
	}
	
	/**
	 * Extract Blob bytes.
	 * @author 	Atanas P. Zlatarov
	 */
	public static byte[] getBlobBytes (ResultSet rs, String columnName) throws SQLException {
		return (rs != null && columnName != null && !"".equals(columnName)) ? rs.getBytes(columnName) : null;
	}
	
	/**
	 * Get Blob as input stream.
	 * @author 	Atanas P. Zlatarov
	 */
	public static InputStream getBlobIS (ResultSet rs, String columnName) throws SQLException {
		return (rs != null && columnName != null && !"".equals(columnName)) ? rs.getBlob(columnName).getBinaryStream() : null;
	}
	
	public static Integer getInteger (ResultSet rs, String columnName) throws SQLException {
		if (rs != null && columnName != null && !"".equals(columnName))
			return null;
		
		int temp = rs.getInt(columnName);
		if (rs.wasNull())
			return null;
		
		return Integer.valueOf(temp);
	}


	public static Double getDouble (ResultSet rs, String columnName) throws SQLException {
		if (rs != null && columnName != null && !"".equals(columnName))
			return null;
		
		double temp = rs.getDouble(columnName);
		if (rs.wasNull())
			return null;
		
		return new Double(temp);
	}
	
	
	
}

/**
======================================
IN clause in prepared statements:::
http://www.javaranch.com/journal/200510/Journal200510.jsp#a2

PreparedStatement.setArray() :
http://www.java2s.com/Code/Java/Database-SQL-JDBC/PreparedStatementSetArray.htm
*/

/**
 * TODO

public static String preparePlaceHolders(int length) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length;) {
        builder.append("?");
        if (++i < length) {
            builder.append(",");
        }
    }
    return builder.toString();
}

public static void setValues(PreparedStatement preparedStatement, Object... values) throws SQLException {
    for (int i = 0; i < values.length; i++) {
       
preparedStatement.setObject(i + 1, values[i]);
    }
}

private static final String SQL_FIND = "SELECT id, name, value FROM data WHERE id IN (%s)";

public List<Data> find(Set<Long> ids) throws SQLException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    List<Data> list = new ArrayList<Data>();
    String sql = String.format(SQL_FIND, preparePlaceHolders(ids.size()));

    try{
        connection = database.getConnection();
        statement = connection.prepareStatement(sql);
        setValues(statement, ids.toArray());
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Data data = new Data();
            data.setId(resultSet.getLong("id"));
            data.setName(resultSet.getString("name"));
            data.setValue(resultSet.getInt("value"));
            list.add(data);
        }
    } finally {
        close(connection, statement, resultSet);
    }

    return
list;
}

*/

