package pageRank;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PrePageRank{
	
	public static class PrePageRankMapper extends Mapper<Object, Text, Text, Text> {
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] tuple = line.split("\t");
			String A = tuple[0];
			
			double pr = 1.0;
			if(tuple.length >= 2){
				String[] array = tuple[1].split(" | ");
				
				for (int i = 0; i < array.length; i++) {
					String[] tmp = array[i].split(",");
					if(tmp.length >= 2){
						String link = tmp[0];
						double linkPr = Double.parseDouble(tmp[1]);
						String prValue = A + "," + pr * linkPr;
						context.write(new Text(link), new Text(prValue));//���֣�A�������Ͷ��Ʊ��
					}
				}
				context.write(new Text(A), new Text("#" + tuple[1]));//A�����֣�#����A�������˼�Ʊ��
			}
		}
	}

	public static class PrePageRankReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			
			String links = "";
			double pagerank = 0.0;
			for (Text value : values) {
				String tmp = value.toString();
				if (tmp.startsWith("#")) {
					links = "\t" + tmp.substring(tmp.indexOf("#") + 1);//���¹������key����˹����������б�
					continue;
				}
				String[] tuple = tmp.split(",");
				if (tuple.length > 1) {
					pagerank += Double.parseDouble(tuple[1]);//ͳ�������˸�key�����Ͷ��Ʊ��
				}
			}
			pagerank = 0.15 + 0.85 * pagerank;//rankֵ
			context.write(key, new Text(String.valueOf(pagerank) + links));
		}
	}
	public static void main(String[] args) throws Exception {
        Configuration conf=new Configuration();
        @SuppressWarnings("deprecation")
		Job job=new Job(conf);
        job.setJarByClass(PageRank.class);
        job.setReducerClass(PrePageRankReducer.class);
        job.setMapperClass(PrePageRankMapper.class);
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        job.waitForCompletion(true);
	}
}