require 'xmlrpc/client'

trac_url = ''

#authentication infomation
username = '' 
password = ''

def create_card_from_ticket ticket
  card = create_card "#{ticket[0]} : #{ticket[3]['summary']}"
  card.content = "#{ticket[3]['description']}"
  type = ticket[3]['type']
  if type == 'defect'
    card.color_type = RED
  elsif type == 'enhancement'
    card.color_type = YELLOW
  end
end

def server_call server
  server.call("ticket.query")
  #server.call("ticket.query","status!=closed&type=defect") # only defect type if you want
end

def create_xmlrpc_path username,password,trac_url
  if username.empty? == false
    trac_url = trac_url + "/login"
    trac_url = trac_url.gsub(/\/\//,'/')
  end

  protocol = 'http'

  # connect by xmlrpc
  xmlrpc_path = "#{protocol}://#{username}:#{password}@#{trac_url}/xmlrpc"
end

if trac_url.empty?
  show_warning "Check trac_url", "Please open board editor(right click and select'edit board') and check trac_url"
  return
end

server = XMLRPC::Client.new2(create_xmlrpc_path username,password,trac_url)

allPages = server_call server

count = 1
monitor.beginTask("get Ticket",allPages.length)
allPages.each do |page|
  monitor.subTask("getting... #{count}/#{allPages.length}")
  ticket = server.call("ticket.get",page)
  create_card_from_ticket ticket  
  monitor.worked 1
  count = count + 1
end