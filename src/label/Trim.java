package label;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Trim {
	public static class TrimMapper extends Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] tuple = line.split("\t");
			if(tuple.length >= 3){
				String keyName = tuple[0];//����
				String label = tuple[1];//��ǩ
				context.write(new Text(label), new Text(keyName));
			}
		}
	}
	
	public static class TrimReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String[] label = key.toString().split(",");
			String keyName = String.format("%-8s", label[0] + "#");
			for(Text value : values){
				context.write(new Text(keyName), new Text(value));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
        Configuration conf=new Configuration();
        @SuppressWarnings("deprecation")
		Job job=new Job(conf);
        job.setJarByClass(Trim.class);
        job.setReducerClass(TrimReducer.class);
        job.setMapperClass(TrimMapper.class);
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true)?0:1);
	}		
}