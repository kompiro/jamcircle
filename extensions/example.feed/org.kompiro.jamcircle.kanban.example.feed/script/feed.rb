require 'open-uri'
require 'rexml/document'

feed_url = "http://rss.news.yahoo.com/rss/topstories"

xml = open(feed_url).read
doc = REXML::Document.new(xml)
doc.each_element('/rss/channel/item') do |elem|
  card = create_card  elem.elements['title'].text
  card.content = elem.elements['description'].text
end
