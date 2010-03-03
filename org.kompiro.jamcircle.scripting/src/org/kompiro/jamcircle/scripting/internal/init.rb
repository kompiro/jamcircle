card = $bsf.lookupBean('card');
lane = $bsf.lookupBean('lane');
event = $bsf.lookupBean('event');
board = $bsf.lookupBean('board');
board_part = $bsf.lookupBean('boardPart');
monitor = $bsf.lookupBean('monitor');

def board_command_executer
  board_command_executer = $bsf.lookupBean('boardCommandExecuter');
  return board_command_executer
end

def create_card(subject=nil)
  created = Card.new
  created.subject = subject
  board_command_executer.add created
  return created
end

def remove_card(target)
  board_command_executer.remove target
  return target
end

def create_lane(status=nil)
  created = Lane.new
  created.status = status
  board_command_executer.add created
  return created
end

def remove_lane(target)
  board_command_executer.remove target
  return target
end

