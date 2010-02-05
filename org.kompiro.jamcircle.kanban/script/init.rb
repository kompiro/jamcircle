require "java"
Card = org.kompiro.jamcircle.kanban.model.mock.Card
Lane = org.kompiro.jamcircle.kanban.model.mock.Lane
User = org.kompiro.jamcircle.kanban.model.mock.User

def create_card(subject=nil)
  board = $bsf.lookupBean('board');
  card = Card.new
  card.subject = subject
  board.add_card card
  return card
end

def create_lane(status=nil)
  board = $bsf.lookupBean('board');
  lane = Lane.new
  lane.status = status
  board.add_lane lane
  return lane
end
