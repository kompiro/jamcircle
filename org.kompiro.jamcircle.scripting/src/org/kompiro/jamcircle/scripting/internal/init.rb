card = $bsf.lookupBean('card');
lane = $bsf.lookupBean('lane');
event = $bsf.lookupBean('event');
board = $bsf.lookupBean('board');
monitor = $bsf.lookupBean('monitor');

def create_card(subject=nil)
  card = Card.new
  card.subject = subject
  board = $bsf.lookupBean('board');
  board.add_card card
  return card
end

def create_lane(status=nil)
  lane = Lane.new
  lane.status = status
  board = $bsf.lookupBean('board');
  board.add_lane lane
  return lane
end