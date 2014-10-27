require 'gooddata'
require 'pry'

client = GoodData.connect
blueprint = GoodData::Model::ProjectBlueprint.build("Acme project") do |p|
  p.add_date_dimension('purchased_on')

  p.add_dataset('flights') do |d|
    d.add_attribute('itinerary_id')
    d.add_attribute('number_of_coupons')
    d.add_attribute('market_id')
    d.add_attribute('origin_airport')
    d.add_attribute('destination_airport')
    d.add_fact('fare')
    d.add_fact('distance')
    d.add_date('purchased_on', :dataset => 'purchased_on')
  end
end

project = client.create_project_from_blueprint(blueprint, auth_token: 'FILL THE TOKEN')
GoodData::with_project(project) do |p|
  flight_data = [
    ['itinerary_id','market_id','number_of_coupons','origin_airport','destination_airport','fare','distance', 'purchased_on'],
    [2014127,201412701,2,'ABE','CLT',319,1414,'1/1/2011'],
    [2014127,201412703,1,'CLT','ABE',319,1414,'1/1/2011']
  ]
  GoodData::Model.upload_data(flight_data, blueprint, 'flights')
end
puts "PROJECT #{project.pid} CREATED"