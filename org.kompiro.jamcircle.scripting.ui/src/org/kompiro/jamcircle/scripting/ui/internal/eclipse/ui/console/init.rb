require 'jruby';
require 'irb';
require 'irb/completion';
Card = org.kompiro.jamcircle.kanban.model.mock.Card; 
Lane = org.kompiro.jamcircle.kanban.model.mock.Lane; 
User = org.kompiro.jamcircle.kanban.model.mock.User;
def board
  return $board_accessor.board
end

def board_command_executer
  board_command_executer = $board_command_executer_accessor.executer;
  return board_command_executer
end

def create_card(subject=nil)
  created = Card.new
  created.subject = subject
  board_command_executer.add_card created
  return created
end

def remove_card(target)
  board_command_executer.remove_card target
  return target
end

def create_lane(status=nil)
  created = Lane.new
  created.status = status
  board_command_executer.add_lane created
  return created
end

def remove_lane(target)
  board_command_executer.remove_lane target
  return target
end

IRB.setup nil;
irb = IRB::Irb.new;
irb.context.prompt_mode=:DEFAULT;
IRB.conf[:IRB_RC].call(irb.context) if IRB.conf[:IRB_RC];
IRB.conf[:MAIN_CONTEXT] = irb.context;
trap("SIGINT") do
  irb.signal_handle;
end;
catch(:IRB_EXIT) do
  irb.eval_input;
end
