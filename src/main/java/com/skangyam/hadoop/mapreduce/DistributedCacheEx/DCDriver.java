package com.skangyam.hadoop.mapreduce.DistributedCacheEx;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
//import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

@SuppressWarnings("deprecation")
public class DCDriver extends Configured implements Tool {

	public int run(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.printf("Usage: %s [generic options] "
					+ "<Emp Input Path> <Output Path> <Dept Directory>\n",
					getClass().getSimpleName());
			return -1;
		}
		
		Configuration conf = new Configuration();
		
		DistributedCache.addCacheFile(new Path(args[2]).toUri(), conf);
		
		Job job = new Job(conf);
		job.setJarByClass(DCDriver.class);
		job.setJobName(this.getClass().getName());
		
		//job.addCacheFiles(new Path(args[2]).toUri());
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(MapperEmpD.class);
		job.setNumReduceTasks(0);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.waitForCompletion(true);
		
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new DCDriver(), args);
		System.exit(exitCode);

	}

}
