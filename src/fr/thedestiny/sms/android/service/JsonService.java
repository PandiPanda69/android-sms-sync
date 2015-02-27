package fr.thedestiny.sms.android.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Service aims to convert input into Json.
 * @author Sébastien
 */
public enum JsonService {

	INSTANCE;
	
	/**
	 * Translate input into json chunk of <em>chunkSize</em> items.
	 * @param input Input to translate
	 * @param chunkSize Size of a chunk (must be > 0)
	 * @return A {@link List} of json encoded object.
	 * @throws IOException Raised by Jackson when an error occurs. See {@link ObjectMapper#writeValueAsString(Object)}
	 */
	public List<String> translateInputIntoChunkedJson(final List<Map<String, String>> input, final int chunkSize) throws IOException {
		int chunkCount = (int) Math.ceil((double) input.size() / (double) chunkSize);
		
		List<String> result = new ArrayList<String>(chunkCount);
		ObjectMapper mapper = new ObjectMapper();
		
		String currentJson;
		for(int i = 0; i < chunkCount; i++) {
			int start = i * chunkSize;
			int end   = (i+1) * chunkSize - 1;
			
			end = end > input.size() ? input.size() : end;
			
			currentJson = mapper.writeValueAsString(input.subList(start, end));
			result.add(currentJson);
		}
		
		return result;
	}
}
