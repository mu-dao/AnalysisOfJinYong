package pageRank;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.*;


public class Sort {
	public static class SortMapper extends Mapper<Object, Text, DoubleWritable, Text> {
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] tuple = line.split("\t");
			String name = tuple[0];
			double pr = Double.parseDouble(tuple[1]);//rankֵ
			context.write(new DoubleWritable(pr), new Text(name));//��rankֵ��Ϊ�ؼ���,hadoop���Զ��Թؼ��ֽ��д�С��������
		}
	}
	
	public static class SortReducer extends Reducer<DoubleWritable, Text, Text, Text> {
		//reduce�׶ξͰ�key��value�ٵ����������(������rankֵ)�ĸ�ʽ�������
		public void reduce(DoubleWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			for(Text value : values){
				String keyName = String.format("%-8s", value.toString());
				//context.write(value, new Text(key.toString()));
				context.write(new Text(keyName), new Text(key.toString()));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
        Configuration conf=new Configuration();
        @SuppressWarnings("deprecation")
		Job job=new Job(conf);
        job.setJarByClass(Sort.class);
        job.setReducerClass(SortReducer.class);
        job.setMapperClass(SortMapper.class);
        
        job.setMapOutputKeyClass(DoubleWritable.class);
        job.setMapOutputValueClass(Text.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true)?0:1);
	}
}
