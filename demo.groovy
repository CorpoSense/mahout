@Grab(group = 'org.apache.mahout', module = 'mahout-core', version = '0.9')

import org.apache.mahout.cf.taste.impl.common.FastByIDMap
import org.apache.mahout.cf.taste.impl.common.FastIDSet
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity

//you can get this data from here: http://files.grouplens.org/datasets/movielens/ml-100k.zip
def mlDir = new File(getClass().protectionDomain.codeSource.location.path).parent+'/ml-100k'
def f = new File("$mlDir/u.data")
assert f.exists()

def m = new FileDataModel(f, ',') {
	void processLine(
			String line, FastByIDMap<?> data, FastByIDMap<FastByIDMap<Long>> timestamps, boolean fromPriorData
	) {
		def newLine = line.split('\t').take(3).join(',')
		super.processLine(newLine, data, timestamps, fromPriorData)
	}

	void processLineWithoutID(
			String line, FastByIDMap<FastIDSet> data, FastByIDMap<FastByIDMap<Long>> timestamps) {
		def newLine = line.split('\t').take(3).join(',')
		try {
			if (newLine && (newLine[0].isNumber()))
				super.processLineWithoutID(newLine, data, timestamps)
		} catch (NoSuchElementException ignore) {
			// if you run into this line, you're probably pushing String to processLineWithoutID, so check newLine's value!
		}
	}
}
def similarity = new TanimotoCoefficientSimilarity(m)
def recommender = new GenericItemBasedRecommender(m, similarity)
def items = new File("$mlDir/u.item").readLines().collectEntries { it.split('\\|').take(2).toList() }

m.itemIDs.each { itemId ->
	def recommendedItems = recommender.mostSimilarItems(itemId, 5)
	println "People who liked '${items[itemId.toString()]}' also liked"
	recommendedItems.each { recommendedItem ->
		println "    (${(recommendedItem.value * 100).intValue()}) ${items[recommendedItem.itemID.toString()]}"
	}
}