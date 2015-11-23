package com.skangyam.hadoop.mapreduce.DistributedCacheEx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;

/**
 * Distributed Cache Mapper
 * To perform a Map-side join
 */
@SuppressWarnings("deprecation")
public class MapperEmpD extends Mapper<LongWritable, Text, Text, Text> 
{
	HashMap<Integer,String> depMap = new HashMap<Integer, String>();
	@Override
	protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
	          throws IOException,InterruptedException{
		//Initiate the Configuration
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.getLocal(conf);
		
		Path[] dataFile = DistributedCache.getLocalCacheFiles(conf);
		//Path[] dataFile = context.getLocalCacheFiles();
		
		//Use BufferedReader to read the file
		System.out.printf("Cache File: %s" + (dataFile[0]).toString());
		BufferedReader cacheReader = new BufferedReader(new FileReader((dataFile[0]).toString()));
		String line;
		while ((line = cacheReader.readLine()) != null) {
			String[] parts = line.split(",");
			depMap.put(Integer.parseInt(parts[0]), parts[1]);
		}
		
		// Close the BufferedReader
		cacheReader.close();
	}
    protected void map(LongWritable key, Text values, Context context)
              throws IOException,InterruptedException
    {
        String[] parts = values.toString().split(",");
        
        Text mapKey = new Text("<"+parts[2]+","+depMap.get(Integer.parseInt(parts[2]))+">");
        Text mapValue = new Text("<"+parts[0]+","+parts[1]+">");
        
        context.write(mapKey, mapValue);
    }
}
